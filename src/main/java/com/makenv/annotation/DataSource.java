package com.makenv.annotation;

import com.makenv.enums.DataBaseType;

import java.lang.annotation.*;
import java.util.Enumeration;

/**
 * Created by wgy on 2016/9/1.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSource {

    DataBaseType name() default DataBaseType.db1;
}
