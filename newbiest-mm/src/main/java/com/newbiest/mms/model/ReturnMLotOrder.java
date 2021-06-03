package com.newbiest.mms.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * 退料单
 * 仓库退料到供应商
 */
@Entity
@DiscriminatorValue(Document.CATEGORY_RETURN_MLOT)
public class ReturnMLotOrder extends Document {

    public static final String GENERATOR_RETURN_MLOT_ORDER_RULE = "CreateReturnMLotOrder";

    @Override
    public String getCategory(){
        return Document.CATEGORY_RETURN_MLOT;
    }
}
