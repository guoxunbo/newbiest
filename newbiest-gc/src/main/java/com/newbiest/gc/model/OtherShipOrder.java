package com.newbiest.gc.model;

import com.newbiest.mms.model.Document;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * 其他发货单
 */
@Entity
@DiscriminatorValue(OtherShipOrder.CATEGORY_DELIVERYB)
public class OtherShipOrder extends Document {

    public static final String CATEGORY_DELIVERYB = "DeliveryB";


}
