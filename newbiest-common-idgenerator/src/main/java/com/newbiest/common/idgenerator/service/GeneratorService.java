package com.newbiest.common.idgenerator.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.common.idgenerator.model.GeneratorRule;
import com.newbiest.common.idgenerator.utils.GeneratorContext;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by guoxunbo on 2018/8/6.
 */
public interface GeneratorService {

    void deleteGeneratorRule(Long ruleRrn, SessionContext sc) throws ClientException;

    String generatorId(long orgRrn, GeneratorContext context) throws ClientException;
    List<String> generatorId(long orgRrn, @NotNull GeneratorRule rule, GeneratorContext context) throws ClientException;
    List<String> generatorId(long orgRrn, @NotNull GeneratorRule rule, boolean isParameterList, GeneratorContext context) throws ClientException;

    List<Integer> getNextSequenceValue(long orgRrn, long generateRrn, String sequenceName, int count) throws ClientException;;
    List<Integer> getNextSequenceValue(long orgRrn, long generateRrn, String sequenceName, int count, int minValue) throws ClientException;
    List<Integer> getNextSequenceValue(long orgRrn, long generateRrn, String sequenceName, int count, int minValue, boolean newTrans) throws ClientException;


}
