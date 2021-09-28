package com.newbiest.mms.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(Document.CATEGORY_SCRAP)
public class ScrapOrder extends Document {

    /**
     * 单据号创建规则
     */
    public static  final String GENERATOR_SCRAP_ORDER_ID_RULE = "createScrapOrderId" ;

    /**
     * 子单号创建规则
     */
    public static  final String GENERATOR_SCRAP_ORDER_LINE_ID_RULE = "createScrapOrderLineId" ;

    /**
     * 默认的报废单据匹配规则
     */
    public static final String DEFAULT_SCRAP_MLOT_DOC_RULE = "ScrapReservedRule";


    @Override
    public String getCategory(){
        return Document.CATEGORY_SCRAP;
    }

}
