package com.newbiest.vanchip;

import com.newbiest.vanchip.service.impl.PackingListFileStrategyServiceImpl;
import liquibase.integration.spring.SpringLiquibase;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

@Slf4j
@Component
@Data
@Configuration
public class VanchipConfiguration {

    /**
     * packingList文件存放位置
     */
    @Value("${vc.packingListPath}")
    private String packingListFilePath;

    @Autowired
    @Lazy
    PackingListFileStrategyServiceImpl packingListFileStrategyService;

    @Bean("vanchipLiquibase")
    public SpringLiquibase liquibase(DataSource dataSource) throws Exception{
        if (log.isInfoEnabled()) {
            log.info("Load Vanchip Liquibase Configuration.");
        }
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("classpath:db/changelog/db.changelog-vanchip.yaml");
        liquibase.setShouldRun(true);
        liquibase.setDropFirst(false);
        return liquibase;
    }

    @PostConstruct
    public void init() {
        //FileStrategyFactory.registerFileStrategy("", packingListFileStrategyService);
    }
}
