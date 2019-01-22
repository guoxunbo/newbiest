package com.newbiest.common.main;

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

@Configuration
@PropertySource(ignoreResourceNotFound = true, value = "classpath:context-${spring.profiles.active}.yml", factory = YmlPropertyLoaderFactory.class)
@ConfigurationProperties(prefix = "context.liquibase")
@Data
@Slf4j
public class ContextLiquibaseConfiguration {

    private String changeLog;

    private boolean enabled;

    private boolean dropFirst;

    @Bean(name="contextLiquibase")
    @ConditionalOnResource(resources = {"classpath:context-${spring.profiles.active}.yml"})
    public SpringLiquibase liquibase(DataSource dataSource) throws Exception{
        if (log.isInfoEnabled()) {
            log.info("Load Context Liquibase Configuration.");
        }
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(changeLog);
        liquibase.setShouldRun(enabled);
        liquibase.setDropFirst(dropFirst);
        return liquibase;
    }

}

