package com.newbiest.common.idgenerator.repository.custom;

import com.newbiest.base.exception.ClientException;
import com.newbiest.common.idgenerator.model.Sequence;

import java.util.List;

/**
 * Created by guoxunbo on 2018/8/6.
 */
public interface SequenceRepositoryCustom {

    Sequence createNewSequence(long orgRrn, String name, long generatorRrn, int minValue) throws ClientException;

    List<Integer> getNextSequenceValue(Sequence sequence, int count, int minValue) throws ClientException;
    List<Integer> getNextSequenceValueNewTrans(Sequence sequence, int count, int minValue) throws ClientException;

}
