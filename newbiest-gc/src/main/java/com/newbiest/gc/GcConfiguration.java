package com.newbiest.gc;

import com.newbiest.base.factory.ModelFactory;
import com.newbiest.gc.model.GCProductSubcode;
import com.newbiest.main.YmlPropertyLoaderFactory;
import liquibase.integration.spring.SpringLiquibase;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

/**
 *
 */
@Configuration
@Data
@Slf4j
@ConfigurationProperties(prefix = "gc.liquibase")
@PropertySource(value = "classpath:gc.yml", factory = YmlPropertyLoaderFactory.class)
public class GcConfiguration {

    private String changeLog;

    private boolean enabled;

    private boolean dropFirst;

    @Bean("gcLiquibase")
    @ConditionalOnResource(resources = {"classpath:gc.yml"})
    public SpringLiquibase liquibase(DataSource dataSource) throws Exception{
        if (log.isInfoEnabled()) {
            log.info("Load GC Liquibase Configuration.");
        }
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(changeLog);
        liquibase.setShouldRun(enabled);
        liquibase.setDropFirst(dropFirst);
        return liquibase;
    }

    @PostConstruct
    public void init() {
        // 注册modelClassLoader
        ModelFactory.registerModelClassLoader(GCProductSubcode.class.getName(), GCProductSubcode.class.getClassLoader());
    }
}
