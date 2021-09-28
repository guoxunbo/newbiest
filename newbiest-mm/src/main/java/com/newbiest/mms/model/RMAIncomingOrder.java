package com.newbiest.mms.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 *RMA 自身原因,由SAP导入
 */
@Entity
@DiscriminatorValue(Document.CATEGORY_RMA_INCOMING)
public class RMAIncomingOrder extends Document {

    public static final String GENERATOR_RMA_INCOMING_ORDER_RULE = "CreateRMAIncomingOrder";

    @Override
    public String getCategory(){
        return Document.CATEGORY_RMA_INCOMING;
    }
}
