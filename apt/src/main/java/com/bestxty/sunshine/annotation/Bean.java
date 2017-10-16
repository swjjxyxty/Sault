package com.bestxty.sunshine.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/16.
 */

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
@Documented
public @interface Bean {

    String value() default "";

    String initMethod() default "";

    String destroyMethod() default "";

}
