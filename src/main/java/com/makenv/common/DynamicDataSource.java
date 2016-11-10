package com.makenv.common;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * Created by wgy on 2016/8/31.
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {

        return DatabaseContextHolder.getDatabaseType();
    }
}
