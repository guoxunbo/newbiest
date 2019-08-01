package com.newbiest.kms.service.impl;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.exception.NewbiestException;
import com.newbiest.base.model.NBBase;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.service.impl.DefaultFileStrategyServiceImpl;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.common.idgenerator.service.GeneratorService;
import com.newbiest.common.idgenerator.utils.GeneratorContext;
import com.newbiest.kms.KmsConfiguration;
import com.newbiest.kms.model.Question;
import com.newbiest.kms.model.QuestionHistory;
import com.newbiest.kms.model.QuestionLine;
import com.newbiest.kms.repository.QuestionLineRepository;
import com.newbiest.kms.repository.QuestionRepository;
import com.newbiest.kms.service.KmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by guoxunbo on 2019-07-29 11:21
 */
@Service
@Transactional
@Slf4j
public class KmsServiceImpl extends DefaultFileStrategyServiceImpl implements KmsService {

    @Autowired
    BaseService baseService;

    @Autowired
    GeneratorService generatorService;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    QuestionLineRepository questionLineRepository;

    @Autowired
    KmsConfiguration kmsConfiguration;

    @Override
    public Question saveQuestion(Question question, SessionContext sc) throws ClientException {
        try {
            if (question.getObjectRrn() == null) {
                String name = generatorQuestionName(question, sc);
                question.setName(name);
            }
            return (Question) baseService.saveEntity(question, sc);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    @Override
    public Question closeQuestion(Question question, SessionContext sc) throws ClientException {
        try {
            if (Question.STATUS_CLOSE.equals(question.getStatus())) {
                throw new ClientException(NewbiestException.COMMON_STATUS_IS_NOT_ALLOW);
            }
            question.setStatus(Question.STATUS_CLOSE);
            question = questionRepository.saveAndFlush(question);
            baseService.saveHistoryEntity(question, QuestionHistory.TRANS_TYPE_CLOSE, sc);
            return question;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public String generatorQuestionName(Question question, SessionContext sc) throws ClientException{
        try {
            GeneratorContext generatorContext = new GeneratorContext();
            generatorContext.setRuleName(Question.CREATE_QUESTION_GENERATOR_NAME);
            generatorContext.setObject(question);
            return generatorService.generatorId(sc.getOrgRrn(), generatorContext);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public List<QuestionLine> getQuestionLineByQuestionRrn(Long questionRrn) throws ClientException{
        return questionLineRepository.findByQuestionRrn(questionRrn);
    }

    @Override
    public String getFilePath(NBBase nbBase) throws ClientException {
        if (nbBase instanceof QuestionLine) {
            return kmsConfiguration.getQuestionFileLinePath();
        }
        return super.getFilePath(nbBase);
    }
}
