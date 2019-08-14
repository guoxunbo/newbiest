package com.newbiest.kms.service.impl;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.exception.NewbiestException;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.utils.ThreadLocalContext;
import com.newbiest.common.idgenerator.service.GeneratorService;
import com.newbiest.common.idgenerator.utils.GeneratorContext;
import com.newbiest.kms.KmsConfiguration;
import com.newbiest.kms.KmsException;
import com.newbiest.kms.model.Question;
import com.newbiest.kms.model.QuestionHistory;
import com.newbiest.kms.model.QuestionLine;
import com.newbiest.kms.repository.QuestionLineRepository;
import com.newbiest.kms.repository.QuestionRepository;
import com.newbiest.kms.service.KmsService;
import com.newbiest.security.model.NBUser;
import com.newbiest.security.service.SecurityService;
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
public class KmsServiceImpl implements KmsService {

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

    @Autowired
    SecurityService securityService;

    @Override
    public Question saveQuestion(Question question) throws ClientException {
        try {
            if (question.getObjectRrn() == null) {
                String name = generatorQuestionName(question);
                question.setName(name);
                NBUser user = securityService.getUserByUsername(ThreadLocalContext.getUsername());
                question.setCreatedUserDept(user.getDepartment());
            }
            return (Question) baseService.saveEntity(question);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 当问题不能复现的时候，改成watching状态，表示未处理，但是当前没的办法。需要观察
     * @param question
     * @return
     * @throws ClientException
     */
    @Override
    public Question watchQuestion(Question question) throws ClientException {
        try {
            if (Question.STATUS_CLOSE.equals(question.getStatus())) {
                throw new ClientException(NewbiestException.COMMON_STATUS_IS_NOT_ALLOW);
            }
            question.setStatus(Question.STATUS_WATCHING);
            question = questionRepository.saveAndFlush(question);
            baseService.saveHistoryEntity(question, QuestionHistory.TRANS_TYPE_WATCHING);
            return question;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 只能是当前创建人才可以关闭问题
     * @param question
     * @return
     * @throws ClientException
     */
    @Override
    public Question closeQuestion(Question question) throws ClientException {
        try {
            if (Question.STATUS_CLOSE.equals(question.getStatus())) {
                throw new ClientException(NewbiestException.COMMON_STATUS_IS_NOT_ALLOW);
            }
            if (!ThreadLocalContext.getUsername().equals(question.getCreatedBy())) {
                throw new ClientException(KmsException.AUTH_IS_NOT_ALLOW);
            }
            question.setStatus(Question.STATUS_CLOSE);
            question = questionRepository.saveAndFlush(question);
            baseService.saveHistoryEntity(question, QuestionHistory.TRANS_TYPE_CLOSE);
            return question;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public String generatorQuestionName(Question question) throws ClientException{
        try {
            GeneratorContext generatorContext = new GeneratorContext();
            generatorContext.setRuleName(Question.CREATE_QUESTION_GENERATOR_NAME);
            generatorContext.setObject(question);
            return generatorService.generatorId(ThreadLocalContext.getOrgRrn(), generatorContext);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public List<QuestionLine> getQuestionLineByQuestionRrn(Long questionRrn) throws ClientException{
        return questionLineRepository.findByQuestionRrn(questionRrn);
    }

}
