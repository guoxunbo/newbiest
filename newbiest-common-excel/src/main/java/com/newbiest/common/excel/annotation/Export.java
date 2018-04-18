package com.newbiest.common.excel.annotation;


import com.newbiest.base.utils.StringUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 是否导出excel的注解
 * Created by guoxunbo on 2018/4/2.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Export {

    /**
     * 验证String是否在当前的提供的数组中
     * 如: {"a", "b", "c"}
     * @return
     */
    String[] in() default {};

    /**
     * 是否允许为空
     * @return
     */
    boolean nullAble() default true;

    /**
     * "greater then" 大于 仅支持数字类型的验证
     * @return
     */
    double gt() default Double.NaN;

    /**
     * "less than" 小于 仅支持数字类型的验证
     * @return
     */
    double lt() default Double.NaN;

    /**
     * "greater then or equals" 大于或等于 仅支持数字类型的验证
     * @return
     */
    double ge() default Double.NaN;

    /**
     * "less then or equals" 小于或等于 仅支持数字类型的验证
     * @return
     */
    double le() default Double.NaN;

}
