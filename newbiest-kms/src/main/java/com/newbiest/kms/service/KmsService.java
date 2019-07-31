package com.newbiest.kms.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.kms.model.Question;
import com.newbiest.kms.model.QuestionLine;

import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by guoxunbo on 2019-07-29 11:20
 */
public interface KmsService {

    Question saveQuestion(Question question, SessionContext sc) throws ClientException;
    Question closeQuestion(Question question, SessionContext sc) throws ClientException;

    List<QuestionLine> getQuestionLineByQuestionRrn(Long questionRrn) throws ClientException;
    QuestionLine uploadQuestionLineFile(QuestionLine questionLine, String fileName, FileInputStream inputStream) throws ClientException;
    void downloadQuetionLineFile(QuestionLine questionLine, OutputStream outputStream) throws ClientException;

}
