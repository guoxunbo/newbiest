package com.newbiest.gc.model;

import com.google.common.collect.Maps;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.common.exception.ContextException;
import com.newbiest.context.model.MergeRuleLine;
import com.newbiest.mms.model.DocumentLine;
import com.newbiest.mms.model.MaterialLot;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.beanutils.PropertyUtils;
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

    public static final String MERGE_DOC_VALIDATE_RULE_ID = "MergeDocRule";  //单据合并验证规则
    public static final String HN_WAREHOUSE_MERGE_DOC_VALIDATE_RULE_ID = "HNWarehouseMergeDocRule";  //湖南仓单据合并验证规则
    public static final String BS_WAREHOUSE_MERGE_DOC_VALIDATE_RULE_ID = "BSWMergeDocRule"; //保税仓单据合并验证规则
    public static final String FT_RETEST_DOC_VALIDATE_RULE_ID = "FtVboxReTestRule"; //FT真空包重测发料单据验证规则
    public static final String MATERIAL_NAME = "materialName";
    public static final String SOURCE_PRODUCT_ID = "sourceProductId";

    /**
     * 目标对象
     */
    private Object targetObject;

    private List<MaterialLot> materialLotList;

    private String ruleId;

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
                                key.append(value.toString().trim());
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
                            //FT重测发料产品型号匹配原产品型号，原产品号为空则匹配原型号
                            if(FT_RETEST_DOC_VALIDATE_RULE_ID.equals(ruleId) && MATERIAL_NAME.equals(fileName) && !StringUtils.isNullOrEmpty(materialLot.getSourceProductId())){
                                fileName = SOURCE_PRODUCT_ID;
                            }
                            String[] fileNameArr = fileName.split(",");
                            if(fileNameArr.length > 1){
                                for(String fileStr : fileNameArr){
                                    Object value = PropertyUtils.getProperty(compareValue, fileStr);
                                    if(value == null){
                                        key.append("");
                                    } else {
                                        key.append(value.toString().trim());
                                    }
                                }
                                key.append(StringUtils.SPLIT_CODE);
                            } else {
                                Object value = PropertyUtils.getProperty(compareValue, fileName);
                                if(value == null){
                                    key.append("" + StringUtils.SPLIT_CODE);
                                } else {
                                    key.append(value.toString().trim());
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
                                compareValue += mLotObject.toString().trim();
                            }
                        }
                    } else {
                        mLotObject = PropertyUtils.getProperty(sourceObject, sourceFileName);
                        if(mLotObject == null){
                            compareValue = "";
                        } else {
                            compareValue = mLotObject.toString().trim();
                        }
                    }
                } catch (Exception e) {
                    throw new ClientParameterException(ContextException.MERGE_BASIC_OBJ_GET_PROPERTY_ERROR, ruleLine.getSourceFiledName());
                }
                try {
                    docLineObject = PropertyUtils.getProperty(targetObject, ruleLine.getTargetFiledName());
                    if(docLineObject != null){
                        targetValue = docLineObject.toString().trim();
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
                } else if(MLotDocRuleLine.COMPARISON_OPERATORS_NULL_OR_EQUALS.equals(ruleLine.getComparisonOperators())){
                    if(StringUtils.isNullOrEmpty(targetValue)){
                        continue;
                    } else {
                        try {
                            Assert.assertEquals(compareValue, targetValue);
                        } catch (AssertionError e) {
                            throw new ClientParameterException(ContextException.MERGE_SOURCE_VALUE_IS_NOT_SAME_TARGET_VALUE, ruleLine.getTargetFiledName(), compareValue, targetValue);
                        }
                    }
                } else {
                    throw new ClientParameterException(ContextException.MERGE_UN_SUPPORT_COMPARISON, ruleLine.getComparisonOperators());
                }
            }
        }
    }

    /**
     * 单据合并信息验证
     * @throws ClientException
     */
    public void validationDocMerge() throws ClientException{
        if (CollectionUtils.isNotEmpty(mLotDocRuleLines)) {
            for (MLotDocRuleLine ruleLine : mLotDocRuleLines) {
                Object sourceValue;
                Object targetValue;
                try {
                    sourceValue = PropertyUtils.getProperty(sourceObject, ruleLine.getSourceFiledName());
                } catch (Exception e) {
                    throw new ClientParameterException(ContextException.MERGE_BASIC_OBJ_GET_PROPERTY_ERROR, ruleLine.getSourceFiledName());
                }
                for (Object compareObject : documentLineList) {
                    try {
                        targetValue = PropertyUtils.getProperty(compareObject, ruleLine.getSourceFiledName());
                    } catch (Exception e) {
                        throw new ClientParameterException(ContextException.MERGE_CHECK_OBJ_GET_PROPERTY_ERROR, ruleLine.getSourceFiledName());
                    }
                    if (MergeRuleLine.COMPARISON_OPERATORS_EQUALS.equals(ruleLine.getComparisonOperators())) {
                        try {
                            if(targetValue == null){
                                targetValue = "";
                            }
                            if (sourceValue == null) {
                                sourceValue = "";
                            }
                            Assert.assertEquals(sourceValue, targetValue);
                        } catch (AssertionError e) {
                            throw new ClientParameterException(ContextException.MERGE_SOURCE_VALUE_IS_NOT_SAME_TARGET_VALUE, ruleLine.getSourceFiledName(), sourceValue, targetValue);
                        }
                    } else {
                        throw new ClientParameterException(ContextException.MERGE_UN_SUPPORT_COMPARISON, ruleLine.getComparisonOperators());
                    }
                }
            }
        }
    }
}
