package com.newbiest.mms.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 *RMA 非自身原因,由WMS导入
 */
@Entity
@DiscriminatorValue(Document.CATEGORY_RMA_INCOMING2)
public class RMAIncomingOrder2 extends Document {

    public static final String GENERATOR_RMA_INCOMING_ORDER2_RULE = "CreateRMAIncomingOrder2";

    @Override
    public String getCategory(){
        return Document.CATEGORY_RMA_INCOMING2;
    }
}
