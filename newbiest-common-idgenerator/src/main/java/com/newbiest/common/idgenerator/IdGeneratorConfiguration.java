package com.newbiest.common.idgenerator;

import com.newbiest.base.factory.ModelFactory;
import com.newbiest.base.ui.model.*;
import com.newbiest.common.idgenerator.model.*;
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
 * Created by guoxunbo on 2019/1/10.
 */
@Configuration
@ConfigurationProperties(prefix = "id-generator.liquibase")
@Data
@PropertySource(value = "classpath:id-generator.yml", factory = YmlPropertyLoaderFactory.class)
@Slf4j
public class IdGeneratorConfiguration {

    private String changeLog;

    private boolean enabled;

    private boolean dropFirst;

    /**
     * 默认的LiquibaseBean
     * @param dataSource
     * @return
     * @throws Exception
     */
    @Bean("idGeneratorLiquibase")
    @ConditionalOnResource(resources = {"classpath:id-generator.yml"})
    public SpringLiquibase liquibase(DataSource dataSource) throws Exception{
        if (log.isInfoEnabled()) {
            log.info("Load IDGenerator Liquibase Configuration.");
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
        ModelFactory.registerModelClassLoader(GeneratorRule.class.getName(), GeneratorRule.class.getClassLoader());
        ModelFactory.registerModelClassLoader(GeneratorRuleLine.class.getName(), GeneratorRuleLine.class.getClassLoader());
        ModelFactory.registerModelClassLoader(Sequence.class.getName(), Sequence.class.getClassLoader());

        ModelFactory.registerModelClassLoader(FixedStringRuleLine.class.getName(), FixedStringRuleLine.class.getClassLoader());
        ModelFactory.registerModelClassLoader(DateRuleLine.class.getName(), DateRuleLine.class.getClassLoader());
        ModelFactory.registerModelClassLoader(SequenceRuleLine.class.getName(), SequenceRuleLine.class.getClassLoader());
        ModelFactory.registerModelClassLoader(VariableRuleLine.class.getName(), VariableRuleLine.class.getClassLoader());

    }
}
