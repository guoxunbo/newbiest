package com.newbiest.mms;

import com.newbiest.base.model.NBHis;
import com.newbiest.mms.model.MaterialHistory;
import com.newbiest.mms.model.Product;
import com.newbiest.mms.model.RawMaterial;
import liquibase.integration.spring.SpringLiquibase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

@Slf4j
@Component
public class MmsConfiguration {

    @Bean("mmsLiquibase")
    public SpringLiquibase liquibase(DataSource dataSource) throws Exception{
        if (log.isInfoEnabled()) {
            log.info("Load MMS Liquibase Configuration.");
        }
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("classpath:db/changelog/db.changelog-mms.yaml");
        liquibase.setShouldRun(true);
        liquibase.setDropFirst(false);
        return liquibase;
    }

    @PostConstruct
    public void init() {
        NBHis.registerHistoryClass(RawMaterial.class, MaterialHistory.class);
        NBHis.registerHistoryClass(Product.class, MaterialHistory.class);
    }

}
