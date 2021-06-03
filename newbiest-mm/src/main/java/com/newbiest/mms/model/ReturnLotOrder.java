package com.newbiest.mms.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * 退货单
 * 发出之后，进行退货
 */
@Entity
@DiscriminatorValue(Document.CATEGORY_RETURN_LOT)
public class ReturnLotOrder extends Document {

    public static final String GENERATOR_RETURN_LOT_ORDER_RULE = "CreateReturnLotOrder";

    @Override
    public String getCategory(){
        return Document.CATEGORY_RETURN_LOT;
    }
}
