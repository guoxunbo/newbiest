package com.newbiest.mms.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * 发货单
 * Created by guoxunbo on 2019-08-30 10:09
 */
@Entity
@DiscriminatorValue(Document.CATEGORY_DELIVERY)
public class DeliveryOrder extends Document {
    public static  final String GENERATOR_DELIVERY_ORDER_ID_RULE = "createDeliveryOrderId" ;
}
