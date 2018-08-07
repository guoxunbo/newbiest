package com.newbiest.common.idgenerator.repository.impl;

import com.google.common.collect.Lists;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.model.NBBase;
import com.newbiest.common.idgenerator.model.Sequence;
import com.newbiest.common.idgenerator.repository.custom.SequenceRepositoryCustom;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by guoxunbo on 2018/8/6.
 */
@Slf4j
public class SequenceRepositoryImpl implements SequenceRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public List<Integer> getNextSequenceValueNewTrans(Sequence sequence, int count, int minValue) throws ClientException {
        return getNextSequenceValue(sequence, count, minValue);
    }

    public List<Integer> getNextSequenceValue(Sequence sequence, int count, int minValue) throws ClientException {
        // 当发生此操作的时候禁止其他人查询实体
        if (em.contains(sequence)) {
            em.refresh(sequence, LockModeType.PESSIMISTIC_WRITE);
        } else {
            sequence = em.find(Sequence.class, sequence.getObjectRrn(), LockModeType.PESSIMISTIC_WRITE);
        }
        int value = sequence.getNextSeq().intValue();
        if (value < minValue) {
            value = minValue;
        }
        sequence.setNextSeq((long)(value + count));
        List<Integer> values = Lists.newArrayList();
        for (int i = 0; i < count; i++){
            values.add(value + i);
        }
        em.merge(sequence);
        return values;
    }
    /**
     * 创建新的一个Sequence
     * @param orgRrn 区域号
     * @param name 名称
     * @param generatorRrn IDGenerator的主键
     * @param minValue 最小值
     * @return
     * @throws ClientException
     */
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Sequence createNewSequence(long orgRrn, String name, long generatorRrn, int minValue) throws ClientException{
        try {
            Sequence sequence = new Sequence();
            sequence.setOrgRrn(orgRrn);
            sequence.setActiveFlag(true);
            sequence.setName(name);
            sequence.setGeneratorRrn(generatorRrn);
            if (minValue > 0) {
                sequence.setNextSeq((long)minValue);
            } else {
                sequence.setNextSeq(1L);
            }
            em.flush();
            return sequence;
        } catch(Exception e) {
            // 只记录日志不处理 因为此处可能并发创建 也不能用锁，一处创建成功即可。
            log.error(e.getMessage(), e);
        }
        return null;
    }

}
