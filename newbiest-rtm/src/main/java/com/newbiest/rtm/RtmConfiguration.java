package com.newbiest.rtm;

import com.newbiest.main.YmlPropertyLoaderFactory;
import liquibase.integration.spring.SpringLiquibase;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;
import java.io.Serializable;

/**
 * Created by guoxunbo on 2019/5/27.
 */
@Configuration
@PropertySource(ignoreResourceNotFound = true, value = "classpath:rtm.yml", factory = YmlPropertyLoaderFactory.class)
@ConfigurationProperties(prefix = "rtm.liquibase")
@Data
@Slf4j
public class RtmConfiguration implements Serializable {

    private String changeLog;

    private boolean enabled;

    private boolean dropFirst;

    @Bean(name="rtmLiquibase")
    @ConditionalOnResource(resources = {"classpath:rtm.yml"})
    public SpringLiquibase liquibase(DataSource dataSource) throws Exception{
        if (log.isInfoEnabled()) {
            log.info("Load RTM Liquibase Configuration.");
        }
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(changeLog);
        liquibase.setShouldRun(enabled);
        liquibase.setDropFirst(dropFirst);
        return liquibase;
    }

}
