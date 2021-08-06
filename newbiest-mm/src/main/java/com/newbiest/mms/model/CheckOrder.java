package com.newbiest.mms.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(Document.CATEGORY_CHECK)
public class CheckOrder extends Document {
    public static  final String GENERATOR_CHECK_ORDER_ID_RULE = "createCheckOrderId" ;

    /**
     * 默认的盘点 备货规则
     */
    public static final String DEFAULT_CHECK_RESERVED_RULE = "DefaultCheckReservedRule";

    @Override
    public String getCategory(){
        return Document.CATEGORY_CHECK;
    }

}
