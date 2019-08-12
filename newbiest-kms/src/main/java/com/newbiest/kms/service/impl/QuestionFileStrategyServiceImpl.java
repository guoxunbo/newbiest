package com.newbiest.kms.service.impl;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.model.NBBase;
import com.newbiest.base.service.impl.DefaultFileStrategyServiceImpl;
import com.newbiest.kms.KmsConfiguration;
import com.newbiest.kms.model.QuestionLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * Created by guoxunbo on 2019-08-08 10:03
 */
@Component
public class QuestionFileStrategyServiceImpl extends DefaultFileStrategyServiceImpl {

    @Autowired
    KmsConfiguration kmsConfiguration;

    @Override
    public String getFilePath(NBBase nbBase) throws ClientException {
        return kmsConfiguration.getQuestionPath();
    }

}
