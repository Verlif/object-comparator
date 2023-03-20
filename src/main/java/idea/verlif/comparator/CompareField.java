package idea.verlif.comparator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CompareField {

    /**
     * 是否忽略
     */
    boolean ignored() default false;

    /**
     * 属性名称
     */
    String value() default "";

    /**
     * 向内层递归
     */
    boolean deep() default false;
}
