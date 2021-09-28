package com.newbiest.mms.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(Document.CATEGORY_CHECK)
public class CheckOrder extends Document {

    public static  final String GENERATOR_CHECK_ORDER_ID_RULE = "CreateCheckOrderId" ;

    public static  final String GENERATOR_CHECK_ORDER_LINE_ID_RULE = "CreateCheckOrderLineId" ;

    /**
     * 批次单据匹配规则-匹配物料和仓库
     */
    public static final String CHECK_MLOT_DOC_RULE_WAREHOUSE_AND_MATERIAL = "CheckMLotDocRule1";

    /**
     * 批次单据匹配规则-匹配仓库
     */
    public static final String CHECK_MLOT_DOC_RULE_WAREHOUSE = "CheckMLotDocRule2";

    /**
     * 批次单据匹配规则-匹配物料
     */
    public static final String CHECK_MLOT_DOC_RULE_MATERIAL = "CheckMLotDocRule3";

    public String getGeneratorOrderIdRule(){
        return GENERATOR_CHECK_ORDER_ID_RULE;
    }

    @Override
    public String getCategory(){
        return Document.CATEGORY_CHECK;
    }

}
