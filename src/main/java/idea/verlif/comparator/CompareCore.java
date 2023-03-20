package idea.verlif.comparator;

import idea.verlif.comparator.diff.Different;
import idea.verlif.comparator.diff.Type;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class CompareCore {

    public static Different compare(Object old, Object now) {
        return compare(null, old, now);
    }

    public static Different compare(String prefix, Object old, Object now) {
        CompareObject oldC = new CompareObject(old);
        CompareObject nowC = new CompareObject(now);
        Set<String> nameList = calcNameList(oldC, nowC);

        return compare(prefix, nameList, oldC, nowC);
    }

    public static Different compare(String prefix, Set<String> nameList, CompareObject old, CompareObject now) {
        if (prefix == null) {
            prefix = "";
        }
        Different different = new Different();

        for (String name : nameList) {
            Field oldF = old.getField(name);
            Field nowF = now.getField(name);

            Different.DiffValue diffValue = compareValue(old, oldF, now, nowF);
            if (diffValue != null) {
                diffValue.setName(prefix + diffValue.getName());
                different.addDiffValue(diffValue);
            } else {
                continue;
            }
            // 判断是否需要嵌套对比
            CompareField compareField = old.getCompareField(oldF);
            if (compareField != null && compareField.deep() && diffValue.getType() == Type.MODIFY_VALUE) {
                Object oldV = old.getValue(oldF);
                Object nowV = now.getValue(nowF);
                if (oldV != null && nowV != null) {
                    different.addDifferent(compare(prefix + name + ".", oldV, nowV));
                }
            }
        }
        return different;
    }

    private static Set<String> calcNameList(CompareObject old, CompareObject now) {
        List<Field> oldFields = old.getComparableFieldList();
        List<Field> nowFields = now.getComparableFieldList();

        Set<String> nameList = new HashSet<>();
        for (Field f : oldFields) {
            nameList.add(old.getFieldName(f));
        }
        for (Field f : nowFields) {
            nameList.add(now.getFieldName(f));
        }
        return nameList;
    }

    private static Different.DiffValue compareValue(CompareObject old, Field oldF, CompareObject now, Field nowF) {
        if (oldF == null && nowF == null) {
            return null;
        }
        Different.DiffValue diffValue = new Different.DiffValue();
        diffValue.setOld(old.getValue(oldF));
        diffValue.setNow(now.getValue(nowF));
        if (oldF == null) {
            diffValue.setType(Type.NEW_FIELD);
            diffValue.setName(now.getFieldName(nowF));
        } else if (nowF == null) {
            diffValue.setType(Type.DROPPED_FIELD);
            diffValue.setName(old.getFieldName(oldF));
        } else {
            diffValue.setName(old.getFieldName(oldF));
            if (oldF.equals(nowF)) {
                // 类型相同
                if (Objects.deepEquals(diffValue.getOld(), diffValue.getNow())) {
                    diffValue.setType(Type.NO);
                } else {
                    diffValue.setType(Type.MODIFY_VALUE);
                }
            } else {
                // 类型不同
                diffValue.setType(Type.MODIFY_TYPE);
            }
        }
        return diffValue;
    }
}
