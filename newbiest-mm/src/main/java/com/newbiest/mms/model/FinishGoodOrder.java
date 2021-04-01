package com.newbiest.mms.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * 成品单
 *
 * */
@Entity
@DiscriminatorValue(Document.CATEGORY_FINISH_GOOD)
public class FinishGoodOrder extends Document {
    public static  final String GENERATOR_FINISH_GOOD_ORDER_ID_RULE = "createFinishGoodOrderId" ;


    @Override
    public String getCategory(){
        return Document.CATEGORY_FINISH_GOOD;
    }
}
