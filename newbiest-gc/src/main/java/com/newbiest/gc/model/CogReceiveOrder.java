package com.newbiest.gc.model;

import com.newbiest.mms.model.Document;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * COG接收单
 * Created by guozhangLuo on 2020-12-02
 */
@Entity
@DiscriminatorValue(CogReceiveOrder.CATEGORY_COG_RECEIVE)
public class CogReceiveOrder extends Document {

    public static final String CATEGORY_COG_RECEIVE = "CogReceive";

}
