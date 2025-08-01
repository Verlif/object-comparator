package idea.verlif.comparator.diff;

public enum Type {
    /**
     * 新属性，从没有属性到有属性，且属性不存在值
     */
    NEW_FIELD,
    /**
     * 新属性，从没有属性到有属性，且属性存在值
     */
    NEW_FIELD_FILL,
    /**
     * 无属性，从有属性到没有属性，且原属性有值
     */
    DROPPED_FIELD,
    /**
     * 无属性，从有属性到没有属性，且原属性无值
     */
    DROPPED_FIELD_NULL,
    /**
     * 值修改
     */
    MODIFY_VALUE,
    /**
     * 旧值为空
     */
    MODIFY_FILL,
    /**
     * 新值为空
     */
    MODIFY_NULL,
    /**
     * 属性类型修改
     */
    MODIFY_TYPE,
    /**
     * 未更改
     */
    NO,
}
