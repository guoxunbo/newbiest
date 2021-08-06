package com.newbiest.mms.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(Document.CATEGORY_SCRAP)
public class ScrapOrder extends Document {

    public static  final String GENERATOR_SCRAP_ORDER_ID_RULE = "createScrapOrderId" ;

    /**
     * 默认的报废 备货规则
     */
    public static final String DEFAULT_SCRAP_RESERVED_RULE = "DefaultScrapReservedRule";


    @Override
    public String getCategory(){
        return Document.CATEGORY_SCRAP;
    }

}
