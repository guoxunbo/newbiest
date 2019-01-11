package com.newbiest.rms.main;

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
@PropertySource(value = "classpath:rms-${spring.profiles.active}.yml", factory = YmlPropertyLoaderFactory.class)
@ConfigurationProperties(prefix = "spring.liquibase")
@Data
@Slf4j
public class RmsLiquibaseConfiguration {

    private String changeLog;

    private boolean enabled;

    private boolean dropFirst;

    @Bean(name="rmsLiquibase")
    @ConditionalOnResource(resources = {"classpath:rms-${spring.profiles.active}.yml"})
    public SpringLiquibase liquibase(DataSource dataSource) throws Exception{
        if (log.isDebugEnabled()) {
            log.debug("Load Rms Liquibase Configuration.");
        }
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(changeLog);
        liquibase.setShouldRun(enabled);
        liquibase.setDropFirst(dropFirst);
        return liquibase;
    }

}

