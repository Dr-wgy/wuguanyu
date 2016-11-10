package com.makenv.common;

import com.makenv.enums.DataBaseType;

/**
 * Created by wgy on 2016/9/1.
 */
public class DatabaseContextHolder {

    private static final ThreadLocal<DataBaseType> contextHolder = new ThreadLocal<>();

    public static void setDatabaseType(DataBaseType type) {

        contextHolder.set(type);
    }

    public static DataBaseType getDatabaseType() {
        return contextHolder.get();
    }

    public static void clearDataSourceType() {
        contextHolder.remove();
    }
}
