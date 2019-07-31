package com.newbiest.kms.service.impl;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.exception.NewbiestException;
import com.newbiest.base.service.BaseService;
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
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.*;
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

    public QuestionLine uploadQuestionLineFile(QuestionLine questionLine, String fileName, FileInputStream inputStream) throws ClientException {
        try {
            questionLine = (QuestionLine) questionLineRepository.findByObjectRrn(questionLine.getObjectRrn());
            questionLine.setFileName(fileName);
            questionLine = questionLineRepository.saveAndFlush(questionLine);

            File file = new File(kmsConfiguration.getQuestionFilePath() + File.separator + fileName);
            Files.createParentDirs(file);

            Files.write(ByteStreams.toByteArray(inputStream), file);
            return questionLine;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public List<QuestionLine> getQuestionLineByQuestionRrn(Long questionRrn) throws ClientException{
        return questionLineRepository.findByQuestionRrn(questionRrn);
    }

    public void downloadQuetionLineFile(QuestionLine questionLine, OutputStream outputStream) throws ClientException {
        questionLine = (QuestionLine) questionLineRepository.findByObjectRrn(questionLine.getObjectRrn());
        File file = new File(kmsConfiguration.getQuestionFilePath() + File.separator + questionLine.getFileName());
        if (!file.exists()) {
//            throw new ClientException(DmsException.CHANGE_SHIFT_FILE_IS_NOT_EXIT);
        }

        try (InputStream inputStream = new FileInputStream(file)) {
            ByteStreams.copy(inputStream, outputStream);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    throw ExceptionManager.handleException(e, log);
                }
            }
        }
    }


}
