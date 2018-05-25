package com.newbiest.main;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.io.Serializable;

/**
 * 数据库连接池
 * Created by guoxunbo on 2017/9/6.
 */

public class DruidConfiguration implements Serializable {

    @Bean
    @Primary  //多数据源中，首先使用被Primary标注的DataSource
    @ConfigurationProperties(prefix = "spring.datasource.druid")
    public DataSource dataSource() {
        return DruidDataSourceBuilder.create().build();
    }

}
