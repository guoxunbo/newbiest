package com.newbiest.mms.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * 接收单
 * Created by guoxunbo on 2020-01-21 13:50
 */
@Entity
@DiscriminatorValue(Document.CATEGORY_RECEIVE)
public class ReceiveOrder extends Document {


}
