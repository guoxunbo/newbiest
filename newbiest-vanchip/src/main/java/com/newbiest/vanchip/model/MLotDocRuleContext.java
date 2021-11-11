package com.newbiest.vanchip.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.PropertyUtils;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.common.exception.ContextException;
import com.newbiest.mms.model.DocumentLine;
import com.newbiest.mms.model.MaterialLot;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Assert;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 物料批次与单据验证容器
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MLotDocRuleContext implements Serializable {

    /**
     * 原始对象
     */
    private Object sourceObject;

    /**
     * 目标对象
     */
    private Object targetObject;

    private List<MaterialLot> materialLotList;

    private List<DocumentLine> documentLineList;

    private List<MLotDocRuleLine> mLotDocRuleLines;

    public Map<String,List<DocumentLine>> validationAndGetDocLine() throws ClientException {
        try {
            Map<String,List<DocumentLine>> documentLineMap = Maps.newHashMap();
            if (CollectionUtils.isNotEmpty(mLotDocRuleLines)) {
                documentLineMap = documentLineList.stream().collect(Collectors.groupingBy(documentLine -> {
                    StringBuffer key = new StringBuffer();
                    Object compareValue = documentLine;
                    for(MLotDocRuleLine ruleLine : mLotDocRuleLines){
                        try {
                            String fileName = ruleLine.getTargetFiledName();
                            Object value = PropertyUtils.getProperty(compareValue, fileName);
                            if(value == null){
                                key.append("");
                                key.append(StringUtils.SPLIT_CODE);
                            } else {
                                key.append(value.toString());
                                key.append(StringUtils.SPLIT_CODE);
                            }
                        } catch (Exception e) {
                            throw new ClientParameterException(ContextException.MERGE_BASIC_OBJ_GET_PROPERTY_ERROR, ruleLine.getTargetFiledName());
                        }
                    }
                    return key.toString();
                }));
            }
            return documentLineMap;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e);
        }
    }

    public Map<String, List<MaterialLot>> validateAndGetMLot() throws ClientException{
        try {
            Map<String,List<MaterialLot>> materialLotMap = Maps.newHashMap();
            if (CollectionUtils.isNotEmpty(mLotDocRuleLines)) {
                materialLotMap = materialLotList.stream().collect(Collectors.groupingBy(materialLot -> {
                    StringBuffer key = new StringBuffer();
                    Object compareValue = materialLot;
                    for(MLotDocRuleLine ruleLine : mLotDocRuleLines){
                        try {
                            String fileName = ruleLine.getSourceFiledName();
                            String[] fileNameArr = fileName.split(",");
                            if(fileNameArr.length > 1){
                                for(String fileStr : fileNameArr){
                                    Object value = PropertyUtils.getProperty(compareValue, fileStr);
                                    if(value == null){
                                        key.append("");
                                    } else {
                                        key.append(value.toString());
                                    }
                                }
                                key.append(StringUtils.SPLIT_CODE);
                            } else {
                                Object value = PropertyUtils.getProperty(compareValue, fileName);
                                if(value == null){
                                    key.append("");
                                    key.append(StringUtils.SPLIT_CODE);
                                } else {
                                    key.append(value.toString());
                                    key.append(StringUtils.SPLIT_CODE);
                                }
                            }
                        } catch (Exception e) {
                            throw new ClientParameterException(ContextException.MERGE_BASIC_OBJ_GET_PROPERTY_ERROR, ruleLine.getSourceFiledName());
                        }
                    }
                    return key.toString();
                }));
            }
            return materialLotMap;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 默认的验证方法。当ruleLines是空的时候，则不进行验证
     */
    public void validation() throws ClientException {
        if (CollectionUtils.isNotEmpty(mLotDocRuleLines)) {
            for (MLotDocRuleLine ruleLine : mLotDocRuleLines) {
                String compareValue = StringUtils.EMPTY;
                String targetValue = StringUtils.EMPTY;
                Object mLotObject;
                Object docLineObject;
                try {
                    String sourceFileName = ruleLine.getSourceFiledName();
                    String[] fileArr = sourceFileName.split(",");
                    if(fileArr.length > 1){
                        for(String fileName : fileArr){
                            mLotObject = PropertyUtils.getProperty(sourceObject, fileName);
                            if(mLotObject == null){
                                compareValue += "";
                            } else {
                                compareValue += mLotObject.toString();
                            }
                        }
                    } else {
                        mLotObject = PropertyUtils.getProperty(sourceObject, sourceFileName);
                        if(mLotObject == null){
                            compareValue = "";
                        } else {
                            compareValue = mLotObject.toString();
                        }
                    }
                } catch (Exception e) {
                    throw new ClientParameterException(ContextException.MERGE_BASIC_OBJ_GET_PROPERTY_ERROR, ruleLine.getSourceFiledName());
                }
                try {
                    docLineObject = PropertyUtils.getProperty(targetObject, ruleLine.getTargetFiledName());
                    if(docLineObject != null){
                        targetValue = docLineObject.toString();
                    }
                } catch (Exception e) {
                    throw new ClientParameterException(ContextException.MERGE_CHECK_OBJ_GET_PROPERTY_ERROR, ruleLine.getTargetFiledName());
                }
                if (MLotDocRuleLine.COMPARISON_OPERATORS_EQUALS.equals(ruleLine.getComparisonOperators())) {
                    try {
                        Assert.assertEquals(compareValue, targetValue);
                    } catch (AssertionError e) {
                        throw new ClientParameterException(ContextException.MERGE_SOURCE_VALUE_IS_NOT_SAME_TARGET_VALUE, ruleLine.getTargetFiledName(), compareValue, targetValue);
                    }
                } else {
                    throw new ClientParameterException(ContextException.MERGE_UN_SUPPORT_COMPARISON, ruleLine.getComparisonOperators());
                }
            }
        }
    }

    public List<MaterialLot> getMLotsByDocRule() throws ClientException {
        try {
            DocumentLine documentLine = documentLineList.get(0);
            if (documentLine == null || CollectionUtils.isNotEmpty(mLotDocRuleLines)){
                return null;
            }
            for (MLotDocRuleLine ruleLine : mLotDocRuleLines) {
                String docLineFileName = ruleLine.getTargetFiledName();
                String docLineFileValue = (String)PropertyUtils.getProperty(documentLine, docLineFileName);

                String mLotFiledName = ruleLine.getSourceFiledName();
                List<MaterialLot> materialLots = Lists.newArrayList();

                for (MaterialLot materialLot: materialLotList){
                    String mLotFileValue = (String)PropertyUtils.getProperty(materialLot, mLotFiledName);
                    if (MLotDocRuleLine.COMPARISON_OPERATORS_EQUALS.equals(ruleLine.getComparisonOperators())) {
                        if (mLotFileValue.equals(docLineFileValue)){

                            materialLots.add(materialLot);
                        }
                    } else if (MLotDocRuleLine.COMPARISON_OPERATORS_CONTAINS.equals(ruleLine.getComparisonOperators())){
                        if (mLotFileValue.contains(docLineFileValue)){

                            materialLots.add(materialLot);
                        }
                    }else {
                        throw new ClientParameterException(ContextException.MERGE_UN_SUPPORT_COMPARISON, ruleLine.getComparisonOperators());
                    }
                }
                if (CollectionUtils.isNotEmpty(materialLots)){
                    return materialLots;
                }
                materialLotList = materialLots;
            }
            return materialLotList;
        }catch (Exception e){
            throw new ClientException(e);
        }
    }

}