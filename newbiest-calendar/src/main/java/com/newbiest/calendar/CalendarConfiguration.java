package com.newbiest.calendar;

import com.newbiest.base.factory.ModelFactory;
import com.newbiest.calendar.model.ChangeShift;
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

@Configuration
@ConfigurationProperties(prefix = "calendar.liquibase")
@Data
@PropertySource(value = "classpath:calendar.yml", factory = YmlPropertyLoaderFactory.class)
@Slf4j
public class CalendarConfiguration {

    private String changeLog;

    private boolean enabled;

    private boolean dropFirst;

    /**
     * 默认的LiquibaseBean
     * @param dataSource
     * @return
     * @throws Exception
     */
    @Bean("calendarLiquibase")
    @ConditionalOnResource(resources = {"classpath:calendar.yml"})
    public SpringLiquibase liquibase(DataSource dataSource) throws Exception {
        if (log.isInfoEnabled()) {
            log.info("Load Calendar Liquibase Configuration.");
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
        //注册modelClassLoader
        ModelFactory.registerModelClassLoader(ChangeShift.class.getName(), ChangeShift.class.getClassLoader());
    }
}
