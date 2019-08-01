package com.newbiest.kms;

import com.newbiest.base.factory.FileStrategyFactory;
import com.newbiest.base.factory.ModelFactory;
import com.newbiest.kms.model.Question;
import com.newbiest.kms.model.QuestionHistory;
import com.newbiest.kms.model.QuestionLine;
import com.newbiest.kms.service.KmsService;
import com.newbiest.main.YmlPropertyLoaderFactory;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;

/**
 * Created by guoxunbo on 2019-08-01 10:14
 */
@Configuration
@ConfigurationProperties(prefix = "kms")
@Data
@PropertySource(value = "classpath:kms.yml", factory = YmlPropertyLoaderFactory.class)
@Slf4j
public class KmsConfiguration {

    public static final String FILE_STRATEGY_NAME = "KMS";

    @Autowired
    KmsService kmsService;

    private String questionFileLinePath;

    @PostConstruct
    public void init() {
        ModelFactory.registerModelClassLoader(Question.class.getName(), Question.class.getClassLoader());
        ModelFactory.registerModelClassLoader(QuestionLine.class.getName(), QuestionLine.class.getClassLoader());
        ModelFactory.registerModelClassLoader(QuestionHistory.class.getName(), QuestionHistory.class.getClassLoader());

        ModelFactory.registerHistoryModelClassLoader(Question.class.getName(), QuestionHistory.class.getClassLoader());
        ModelFactory.registerHistoryClassName(Question.class.getName(), QuestionHistory.class.getName());

        FileStrategyFactory.registerFileStrategy(FILE_STRATEGY_NAME, kmsService);
    }
}
