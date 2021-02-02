package com.newbiest.mms.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * 来料单
 * @author guoxunbo
 * @date 12/24/20 1:29 PM
 */

@Entity
@DiscriminatorValue(Document.CATEGORY_INCOMING)
public class IncomingOrder extends Document {

    public static final String GENERATOR_INCOMING_ORDER_ID_RULE = "CreateIncomingOrder";

    @Override
    public String getCategory(){
        return Document.CATEGORY_INCOMING;
    }
}
