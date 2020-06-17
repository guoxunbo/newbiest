package com.newbiest.gc.model;

import com.newbiest.mms.model.Document;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * 其他出货单
 */
@Entity
@DiscriminatorValue(OtherStockOutOrder.CATEGORY_DELIVERYA)
public class OtherStockOutOrder extends Document {

    public static final String CATEGORY_DELIVERYA = "DeliveryA";


}
