package com.newbiest.context.model;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.common.exception.ContextException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Matcher;
import org.junit.Assert;

import java.io.Serializable;
import java.util.List;

/**
 * 合批规则容器定义
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class MergeRuleContext implements Serializable {

    /**
     * 原始对象
     */
    private Object baseObject;

    /**
     * 需要验证的对象
     */
    private List<? extends Object> compareObjects;

    /**
     * 验证规则
     */
    private List<MergeRuleLine> mergeRuleLines;

    /**
     * 默认的验证方法。当ruleLines是空的时候，则不进行验证
     */
    public void validation() throws ClientException {
        if (CollectionUtils.isNotEmpty(mergeRuleLines)) {
            for (MergeRuleLine ruleLine : mergeRuleLines) {
                Object compareValue;
                Object targetValue;
                try {
                    compareValue = PropertyUtils.getProperty(baseObject, ruleLine.getFiledName());
                } catch (Exception e) {
                    throw new ClientParameterException(ContextException.MERGE_BASIC_OBJ_GET_PROPERTY_ERROR, ruleLine.getFiledName());
                }
                for (Object compareObject : compareObjects) {
                    try {
                        targetValue = PropertyUtils.getProperty(compareObject, ruleLine.getFiledName());
                    } catch (Exception e) {
                        throw new ClientParameterException(ContextException.MERGE_CHECK_OBJ_GET_PROPERTY_ERROR, ruleLine.getFiledName());
                    }
                    if (MergeRuleLine.COMPARISON_OPERATORS_EQUALS.equals(ruleLine.getComparisonOperators())) {
                        try {
                            if (compareValue == null) {
                                compareValue = "";
                            }
                            if(targetValue == null){
                                targetValue = "";
                            }
                            Assert.assertEquals(compareValue, targetValue);
                        } catch (AssertionError e) {
                            throw new ClientParameterException(ContextException.MERGE_SOURCE_VALUE_IS_NOT_SAME_TARGET_VALUE, ruleLine.getFiledName(), compareValue, targetValue);
                        }
                    } else {
                        throw new ClientParameterException(ContextException.MERGE_UN_SUPPORT_COMPARISON, ruleLine.getComparisonOperators());
                    }
                }
            }
        }
    }

    public boolean validateMLot() throws ClientException{
        try {
            boolean falg = true;
            if (CollectionUtils.isNotEmpty(mergeRuleLines)) {
                for (MergeRuleLine ruleLine : mergeRuleLines) {
                    if(!falg){
                        break;
                    }
                    Object compareValue;
                    Object targetValue;
                    try {
                        compareValue = PropertyUtils.getProperty(baseObject, ruleLine.getFiledName());
                    } catch (Exception e) {
                        throw new ClientParameterException(ContextException.MERGE_BASIC_OBJ_GET_PROPERTY_ERROR, ruleLine.getFiledName());
                    }
                    for (Object compareObject : compareObjects) {
                        try {
                            targetValue = PropertyUtils.getProperty(compareObject, ruleLine.getFiledName());
                        } catch (Exception e) {
                            throw new ClientParameterException(ContextException.MERGE_CHECK_OBJ_GET_PROPERTY_ERROR, ruleLine.getFiledName());
                        }
                        if (MergeRuleLine.COMPARISON_OPERATORS_EQUALS.equals(ruleLine.getComparisonOperators())) {
                            if (compareValue == null) {
                                compareValue = "";
                            }
                            if(targetValue == null){
                                targetValue = "";
                            }
                            if(!compareValue.equals(targetValue)){
                                falg = false;
                                break;
                            }
                        } else {
                            throw new ClientParameterException(ContextException.MERGE_UN_SUPPORT_COMPARISON, ruleLine.getComparisonOperators());
                        }
                    }
                }
            }
            return falg;
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

}
