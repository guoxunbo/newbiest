package com.newbiest.base.factory;

import org.springframework.stereotype.Component;

/**
 * Created by guoxunbo on 2018/7/4.
 */
@Component
public class SqlBuilderFactory {

    public SqlBuilder createSqlBuilder() {
        return new SqlBuilder();
    }

}
