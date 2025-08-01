package idea.verlif.comparator;

import idea.verlif.comparator.diff.DiffValue;
import idea.verlif.comparator.diff.Different;
import idea.verlif.comparator.diff.Type;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 对比核心类
 */
public class CompareCore {

    private final static EqualJudge<Object> EQUAL_JUDGE = new DefaultEqualJudge();

    private final Map<Class<?>, EqualJudge<?>> equalJudgeMap;

    public CompareCore() {
        equalJudgeMap = new HashMap<>();
    }

    /**
     * 添加类型判断器
     *
     * @param target     目标类
     * @param equalJudge 类值判断器
     */
    public <T> void addEqualJudge(Class<T> target, EqualJudge<T> equalJudge) {
        equalJudgeMap.put(target, equalJudge);
    }

    /**
     * 对比两个对象
     *
     * @param old 源对象
     * @param now 新对象
     * @return 对比结果
     */
    public Different compare(Object old, Object now) {
        if (old != null && now != null
                && isSimpleClass(old.getClass()) && isSimpleClass(now.getClass())) {
            Different different = new Different();
            DiffValue diffValue = new DiffValue();
            if (old.getClass() != now.getClass()) {
                diffValue.setType(Type.MODIFY_TYPE);
            } else if (Objects.equals(old, now)) {
                diffValue.setType(Type.MODIFY_VALUE);
            }
            diffValue.setOld(old);
            diffValue.setNow(now);
            different.addDiffValue(diffValue);
        }
        return compare(null, old, now);
    }

    /**
     * 对比两个对象
     *
     * @param old 源对象
     * @param now 新对象
     * @return 对比结果
     */
    public Different compare(CompareObject old, CompareObject now) {
        return compare(null, old, now);
    }

    public List<Different> compareList(List<?> old, List<?> now) {
        return compareList(null, old, now, null);
    }

    public List<Different> compareList(List<?> old, List<?> now, List<String> equalKeyNames) {
        return compareList(null, old, now, equalKeyNames);
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

    private boolean isSimpleClass(Class<?> clazz) {
        return clazz.isPrimitive()
                || clazz == String.class
                || clazz == Integer.class
                || clazz == Long.class
                || clazz == Double.class
                || clazz == Float.class
                || clazz == Byte.class
                || clazz == Short.class
                || clazz == Character.class
                || clazz == Boolean.class;
    }

    /**
     * 对比两个对象
     *
     * @param prefix 对比前缀
     * @param oldC   源对象
     * @param nowC   新对象
     * @return 对比结果
     */
    public Different compare(String prefix, CompareObject oldC, CompareObject nowC) {
        Set<String> nameList = calcNameList(oldC, nowC);
        return compare(prefix, nameList, oldC, nowC);
    }

    /**
     * 对比两个集合
     *
     * @param prefix 对比前缀
     * @param old    源对象
     * @param now    新对象
     * @return 对比结果
     */
    public List<Different> compareList(String prefix, List<?> old, List<?> now, List<String> equalKeyNames) {
        List<Different> differences = new ArrayList<>();
        if (old == null || old.isEmpty()) {
            // 旧数据为空列表，则只遍历新数据
            if (now == null) {
                return differences;
            }
            for (Object o : now) {
                Different compare = compare(null, o);
                differences.add(compare);
            }
            return differences;
        }
        if (now.isEmpty()) {
            // 新数据为空，则只遍历旧列表
            for (Object o : old) {
                Different compare = compare(o, null);
                differences.add(compare);
            }
            return differences;
        }
        if (equalKeyNames == null) {
            equalKeyNames = new ArrayList<>();
        }
        // 双列表都有值
        Object oldFirstValue = old.get(0);
        Object nowFirstValue = now.get(0);
        if (isSimpleClass(oldFirstValue.getClass()) && isSimpleClass(nowFirstValue.getClass())) {
            // 简单类型
            return compareSimple(old, now);
        }
        CompareObject oldFi = new CompareObject(oldFirstValue);
        CompareObject nowFi = new CompareObject(nowFirstValue);
        Set<String> nameList = calcNameList(oldFi, nowFi);
        // 对双列表进行排序，按旧列表顺序
        List<CompareObject> newOldList = new ArrayList<>(old.size());
        List<CompareObject> newNowList = new ArrayList<>(old.size());
        List<CompareObject> oldCList = new ArrayList<>(old.size());
        for (Object o : old) {
            oldCList.add(new CompareObject(o));
        }
        List<CompareObject> nowCList = new ArrayList<>(now.size());
        for (Object o : now) {
            nowCList.add(new CompareObject(o));
        }
        for (CompareObject oldObject : oldCList) {
            // 找到横向对比的项
            EQUAL_JUDGE:
            for (CompareObject nowObject : nowCList) {
                for (String equalKey : equalKeyNames) {
                    Object oldVal = oldObject.getValue(equalKey);
                    Object nowVal = nowObject.getValue(equalKey);
                    if (!Objects.equals(oldVal, nowVal)) {
                        continue EQUAL_JUDGE;
                    }
                }
                newNowList.add(nowObject);
            }
            newOldList.add(oldObject);
            if (newOldList.size() > newNowList.size()) {
                newNowList.add(new CompareObject(null));
            }
        }
        // 新数据组补充未被匹配的数据
        newNowList.addAll(nowCList.stream().filter(o -> !newNowList.contains(o)).collect(Collectors.toList()));
        int i = 0;
        for (; i < newOldList.size() && i < newNowList.size(); i++) {
            differences.add(compare(prefix, nameList, newOldList.get(i), newNowList.get(i)));
        }
        if (i < newOldList.size() - 1) {
            // 旧数据更多
            for (int i1 = i; i1 < newOldList.size(); i1++) {
                Different compare = compare(null, newOldList.get(i), new CompareObject(null));
                differences.add(compare);
            }
        } else {
            // 新数据更多
            for (int i1 = i; i1 < newNowList.size(); i1++) {
                Different compare = compare(null, new CompareObject(null), newNowList.get(i));
                differences.add(compare);
            }
        }
        return differences;
    }

    private List<Different> compareSimple(List<?> old, List<?> now) {
        List<Different> differences = new ArrayList<>();
        for (Object o : old) {
            int i = now.indexOf(o);
            if (i < 0) {
                // 新数据缺失
                DiffValue diffValue = new DiffValue();
                diffValue.setType(Type.MODIFY_NULL);
                diffValue.setOld(o);
                diffValue.setNow(null);
                diffValue.setName(o.toString());
                Different e = new Different();
                e.addDiffValue(diffValue);
                differences.add(e);
            } else {
                now.remove(i);
            }
        }
        for (Object o : now) {
            int i = old.indexOf(o);
            if (i < 0) {
                // 新数据缺失
                DiffValue diffValue = new DiffValue();
                diffValue.setType(Type.MODIFY_FILL);
                diffValue.setOld(null);
                diffValue.setNow(o);
                diffValue.setName(o.toString());
                Different e = new Different();
                e.addDiffValue(diffValue);
                differences.add(e);
            }
        }
        return differences;
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

    private Set<String> calcNameList(CompareObject... objs) {
        Set<String> nameList = new HashSet<>();
        for (CompareObject obj : objs) {
            if (obj == null) {
                continue;
            }
            List<Field> fieldList = obj.getComparableFieldList();
            for (Field field : fieldList) {
                nameList.add(obj.getFieldName(field));
            }
        }
        return nameList;
    }

    private DiffValue compareValue(CompareObject old, Field oldF, CompareObject now, Field nowF) {
        if (oldF == null && nowF == null) {
            return null;
        }
        DiffValue diffValue = new DiffValue();
        Object oldValue = old.getValue(oldF);
        diffValue.setOld(oldValue);
        Object nowValue = now.getValue(nowF);
        diffValue.setNow(nowValue);
        if (oldF == null) {
            if (nowValue != null) {
                diffValue.setType(Type.NEW_FIELD_FILL);
            } else {
                diffValue.setType(Type.NEW_FIELD);
            }
            diffValue.setName(now.getFieldName(nowF));
        } else if (nowF == null) {
            if (oldValue != null) {
                diffValue.setType(Type.DROPPED_FIELD);
            } else {
                diffValue.setType(Type.DROPPED_FIELD_NULL);
            }
            diffValue.setName(old.getFieldName(oldF));
        } else {
            diffValue.setName(old.getFieldName(oldF));
            if (oldF.equals(nowF)) {
                Class<?> targetCla = oldF.getType();
                EqualJudge<Object> equalJudge = (EqualJudge<Object>) equalJudgeMap.get(targetCla);
                if (equalJudge == null) {
                    equalJudge = EQUAL_JUDGE;
                    equalJudgeMap.put(targetCla, equalJudge);
                }
                diffValue.setType(equalJudge.equals(diffValue.getOld(), diffValue.getNow()));
            } else {
                // 类型不同
                diffValue.setType(Type.MODIFY_TYPE);
            }
        }
        return diffValue;
    }

    private static final class DefaultEqualJudge implements EqualJudge<Object> {

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
