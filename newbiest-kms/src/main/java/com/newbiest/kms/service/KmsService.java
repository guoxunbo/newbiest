package com.newbiest.kms.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.kms.model.Question;
import com.newbiest.kms.model.QuestionLine;

import java.util.List;

/**
 * Created by guoxunbo on 2019-07-29 11:20
 */
public interface KmsService {

    QuestionLine saveQuestionLine(QuestionLine questionLine) throws ClientException;

    Question saveQuestion(Question question) throws ClientException;
    Question closeQuestion(Question question) throws ClientException;
    Question watchQuestion(Question question) throws ClientException;

    List<QuestionLine> getQuestionLineByQuestionRrn(Long questionRrn) throws ClientException;
}
