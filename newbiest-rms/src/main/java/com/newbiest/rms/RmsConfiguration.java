package com.newbiest.rms;

import com.newbiest.base.core.YmlPropertyLoaderFactory;
import com.newbiest.base.factory.ModelFactory;
import com.newbiest.rms.model.*;
import liquibase.integration.spring.SpringLiquibase;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

/**
 * Created by guoxunbo on 2019/1/14.
 */
@Component
@Slf4j
@ConfigurationProperties(prefix = "rms")
@PropertySource(value = "classpath:rms.yml", factory = YmlPropertyLoaderFactory.class)
@Data
public class RmsConfiguration {

    private boolean sendNotification;

    @PostConstruct
    public void init() {
        //注册modelClassLoader
        ModelFactory.registerModelClassLoader(Equipment.class.getName(), Equipment.class.getClassLoader());
        ModelFactory.registerModelClassLoader(Recipe.class.getName(), Recipe.class.getClassLoader());
        ModelFactory.registerModelClassLoader(RecipeEquipment.class.getName(), RecipeEquipment.class.getClassLoader());
        ModelFactory.registerModelClassLoader(RecipeEquipmentHis.class.getName(), RecipeEquipmentHis.class.getClassLoader());
        ModelFactory.registerModelClassLoader(RecipeEquipmentParameter.class.getName(), RecipeEquipmentParameter.class.getClassLoader());

        // 注册历史
        ModelFactory.registerHistoryModelClassLoader(RecipeEquipment.class.getName(), RecipeEquipmentHis.class.getClassLoader());
        ModelFactory.registerHistoryClassName(RecipeEquipment.class.getName(), RecipeEquipmentHis.class.getName());

    }

    @Bean(name="rmsLiquibase")
    public SpringLiquibase liquibase(DataSource dataSource) throws Exception{
        if (log.isInfoEnabled()) {
            log.info("Load Rms Liquibase Configuration.");
        }
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("classpath:db/changelog/db.changelog-rms.yaml");
        liquibase.setShouldRun(true);
        liquibase.setDropFirst(false);
        return liquibase;
    }
}
