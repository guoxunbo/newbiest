package com.newbiest.mms.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * 退料单
 */

@Entity
@DiscriminatorValue(Document.CATEGORY_RETURN)
public class ReturnMLotOrder extends Document {

    public static final String GENERATOR_RETURN_MLOT_ORDER_ID_RULE = "CreateReturnMLotOrder";

}
