package com.newbiest.common.idgenerator.service.impl;

import com.google.common.collect.Lists;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.ui.service.UIService;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.common.idgenerator.exception.GeneratorExceptions;
import com.newbiest.common.idgenerator.model.GeneratorRule;
import com.newbiest.common.idgenerator.model.GeneratorRuleLine;
import com.newbiest.common.idgenerator.model.Sequence;
import com.newbiest.common.idgenerator.repository.GeneratorRuleLineRepository;
import com.newbiest.common.idgenerator.repository.GeneratorRuleRepository;
import com.newbiest.common.idgenerator.repository.SequenceRepository;
import com.newbiest.common.idgenerator.service.GeneratorService;
import com.newbiest.common.idgenerator.utils.GeneratorContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by guoxunbo on 2018/8/6.
 */
@Service
@Transactional
@Slf4j
public class GeneratorServiceImpl implements GeneratorService {

    @Autowired
    private BaseService baseService;

    @Autowired
    private UIService uiService;

    @Autowired
    private SequenceRepository sequenceRepository;

    @Autowired
    private GeneratorRuleRepository generatorRuleRepository;

    @Autowired
    private GeneratorRuleLineRepository generatorRuleLineRepository;

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public String generatorId(long orgRrn, GeneratorContext context) throws ClientException {
        try {
            List<GeneratorRule> rules = (List<GeneratorRule>) generatorRuleRepository.findByNameAndOrgRrn(context.getRuleName(), orgRrn);
            if (!CollectionUtils.isNotEmpty(rules)) {
                throw new ClientParameterException(GeneratorExceptions.COM_GENERATOR_RULE_IS_NOT_EXIST, context.getRuleName());
            }
            return generatorId(orgRrn, rules.get(0), context).get(0);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public List<String> generatorId(long orgRrn, GeneratorRule rule, GeneratorContext context) throws ClientException {
        return generatorId(orgRrn, rule, false, context);
    }

    /**
     * 批量生成序号
     * @param orgRrn 区域号
     * @param rule 序号生成规则
     * @param isParameterList 是否使用参数List处理批量生成序号
     * @param context 序号生成上下文
     *
     * @return 生成的序号
     */
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public List<String> generatorId(long orgRrn, @NotNull GeneratorRule rule, boolean isParameterList, GeneratorContext context) throws ClientException {
        try {
            context.setBaseService(baseService);
            context.setUiService(uiService);
            context.setGeneratorService(this);

            List<String> ids = Lists.newLinkedList();
            List<GeneratorRuleLine> ruleLines = rule.getRuleLines();
            for (int i = 0; i < context.getCount(); i++){
                if (isParameterList) {
                    context.setCurrentIndex(i);
                }
                for (GeneratorRuleLine line : ruleLines){
                    context.addIdSegments(line.generator(context));
                }
                String idPrefix = context.getIdPrefix();
                context.getIdSegments().clear();
                ids.add(idPrefix);
            }
            return ids;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 根据对应的Generator及NBSequence的名字 获得所对应的下一个Sequence值
     * 如果没有则创建一个新的记录 新开事务进行处理
     *
     * @param orgRrn 所对应的区域
     * @param generatorLineRrn 所对应的GeneratorLine的ObjectRrn
     * @param sequenceName Sequence的名字
     * @param count 所需要获得Sequence数量
     */
    @Override
    public List<Integer> getNextSequenceValue(long orgRrn, long generatorLineRrn, String sequenceName, int count) throws ClientException {
        return getNextSequenceValue(orgRrn, generatorLineRrn, sequenceName, count, 0, true);
    }

    /**
     * 根据对应的Generator及Sequence的名字 获得所对应的下一个Sequence值
     * 如果没有则创建一个新的记录 新开事务进行处理
     * @param orgRrn 所对应的区域
     * @param generatorLineRrn 所对应的GeneratorLine的ObjectRrn
     * @param sequenceName Sequence的名字
     * @param count 所需要获得Sequence数量
     * @param minValue 最小Seq值,所返回的Seq必须大于等于minValue
     */
    @Override
    public List<Integer> getNextSequenceValue(long orgRrn, long generatorLineRrn, String sequenceName, int count, int minValue) throws ClientException {
        return getNextSequenceValue(orgRrn, generatorLineRrn, sequenceName, count, minValue, true);
    }

    /**
     * 根据对应的Generator及Sequence的名字
     * 获得所对应的下一个Sequence值
     * 如果没有则创建一个新的记录
     *
     * @param orgRrn 所对应的区域
     * @param generatorLineRrn 所对应的GeneratorLine的ObjectRrn
     * @param sequenceName Sequence的名字
     * @param count 所需要获得Sequence数量
     * @param minValue 最小Seq值,所返回的Seq必须大于等于minValue
     * @param newTrans 是否在新的事务中创建Sequence(默认为ture),防止并发锁
     */
    @Override
    public List<Integer> getNextSequenceValue(long orgRrn, long generatorLineRrn, String sequenceName, int count, int minValue, boolean newTrans) throws ClientException {
        Sequence sequence = sequenceRepository.getByNameAndGeneratorLineRrn(sequenceName, generatorLineRrn);
        if (sequence == null) {
            // 创建Sequence 有可能多个同时创建 故在此进行查询。
            sequence = sequenceRepository.createNewSequence(orgRrn, sequenceName, generatorLineRrn, minValue);
            if (sequence == null) {
                sequence = sequenceRepository.getByNameAndGeneratorLineRrn(sequenceName, generatorLineRrn);
                if (sequence == null) {
                    throw new ClientException(GeneratorExceptions.COM_GENERATOR_ID_SEQUENCE_ERROR);
                }
            }
        }
        if (newTrans) {
            return sequenceRepository.getNextSequenceValueNewTrans(sequence, count, minValue);
        } else {
            return sequenceRepository.getNextSequenceValue(sequence, count, minValue);
        }
    }

    /**
     * 删除GeneratorRule
     * @param ruleRrn 主键
     * @throws ClientException
     */
    public void deleteGeneratorRule(Long ruleRrn, SessionContext sc) throws ClientException{
        try {
            // 删除line
            generatorRuleLineRepository.deleteByRuleRrn(ruleRrn);

            generatorRuleRepository.deleteById(ruleRrn);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }
}
