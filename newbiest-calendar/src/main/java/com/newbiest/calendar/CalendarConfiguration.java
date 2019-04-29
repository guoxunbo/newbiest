package com.newbiest.calendar;

import com.newbiest.base.factory.ModelFactory;
import com.newbiest.calendar.model.*;
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
        ModelFactory.registerModelClassLoader(ChangeShiftEqpStatus.class.getName(), ChangeShiftEqpStatus.class.getClassLoader());
        ModelFactory.registerModelClassLoader(ChangeShiftEqpCommission.class.getName(), ChangeShiftEqpCommission.class.getClassLoader());
        ModelFactory.registerModelClassLoader(ChangeShiftFaCaseAnalyse.class.getName(), ChangeShiftFaCaseAnalyse.class.getClassLoader());
        ModelFactory.registerModelClassLoader(ChangeShiftSecurityInfo.class.getName(), ChangeShiftSecurityInfo.class.getClassLoader());
        ModelFactory.registerModelClassLoader(ChangeShiftEsRunInfo.class.getName(), ChangeShiftEsRunInfo.class.getClassLoader());
        ModelFactory.registerModelClassLoader(ChangeShiftHoldLot.class.getName(), ChangeShiftHoldLot.class.getClassLoader());
        ModelFactory.registerModelClassLoader(ChangeShiftProcessInfo.class.getName(), ChangeShiftProcessInfo.class.getClassLoader());
        ModelFactory.registerModelClassLoader(ChangeShiftEvaEquipmentInfo.class.getName(), ChangeShiftEvaEquipmentInfo.class.getClassLoader());
        ModelFactory.registerModelClassLoader(ChangeShiftPickInfo.class.getName(), ChangeShiftPickInfo.class.getClassLoader());

        ModelFactory.registerHistoryModelClassLoader(ChangeShift.class.getName(), ChangeShiftHistory.class.getClassLoader());
        ModelFactory.registerHistoryClassName(ChangeShift.class.getName(), ChangeShiftHistory.class.getName());
    }

}
