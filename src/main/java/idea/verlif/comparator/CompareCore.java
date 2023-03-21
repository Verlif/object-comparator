package idea.verlif.comparator;

import idea.verlif.comparator.diff.DiffValue;
import idea.verlif.comparator.diff.Different;
import idea.verlif.comparator.diff.Type;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 对比核心类
 */
public class CompareCore {

    private EqualJudge equalJudge;

    public void setEqualJudge(EqualJudge equalJudge) {
        this.equalJudge = equalJudge;
    }

    /**
     * 对比两个对象
     *
     * @param old 源对象
     * @param now 新对象
     * @return 对比结果
     */
    public Different compare(Object old, Object now) {
        return compare(null, old, now);
    }

    /**
     * 对比两个对象
     *
     * @param prefix 对比前缀
     * @param old    源对象
     * @param now    新对象
     * @return 对比结果
     */
    public Different compare(String prefix, Object old, Object now) {
        CompareObject oldC = new CompareObject(old);
        CompareObject nowC = new CompareObject(now);
        Set<String> nameList = calcNameList(oldC, nowC);

        return compare(prefix, nameList, oldC, nowC);
    }

    public Different compare(String prefix, Set<String> nameList, CompareObject old, CompareObject now) {
        if (prefix == null) {
            prefix = "";
        }
        Different different = new Different();

        for (String name : nameList) {
            Field oldF = old.getField(name);
            Field nowF = now.getField(name);

            DiffValue diffValue = compareValue(old, oldF, now, nowF);
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

    private Set<String> calcNameList(CompareObject old, CompareObject now) {
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

    private DiffValue compareValue(CompareObject old, Field oldF, CompareObject now, Field nowF) {
        if (oldF == null && nowF == null) {
            return null;
        }
        if (equalJudge == null) {
            equalJudge = new DefaultEqualJudge();
        }
        DiffValue diffValue = new DiffValue();
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
                diffValue.setType(equalJudge.equals(diffValue.getOld(), diffValue.getNow()));
            } else {
                // 类型不同
                diffValue.setType(Type.MODIFY_TYPE);
            }
        }
        return diffValue;
    }

    private static final class DefaultEqualJudge implements EqualJudge {

        @Override
        public Type equals(Object old, Object now) {
            if (old == null) {
                if (now == null) {
                    return Type.NO;
                } else {
                    return Type.MODIFY_FILL;
                }
            }
            if (now == null) {
                return Type.MODIFY_NULL;
            }
            if (old.equals(now)) {
                return Type.NO;
            } else {
                return Type.MODIFY_VALUE;
            }
        }
    }
}
