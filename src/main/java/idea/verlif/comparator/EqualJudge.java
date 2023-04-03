package idea.verlif.comparator;

import idea.verlif.comparator.diff.Type;

public interface EqualJudge<T> {

    /**
     * 对象是否相等
     *
     * @param old 源对象
     * @param now 目标对象
     * @return 源对象与目标对象是否相等
     */
    Type equals(T old, T now);
}
