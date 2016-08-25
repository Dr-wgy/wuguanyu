package com.makenv.annotation;

import java.lang.annotation.*;

/**
 * Created by wgy on 2016/8/8.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SelfAnnnotation {
}
