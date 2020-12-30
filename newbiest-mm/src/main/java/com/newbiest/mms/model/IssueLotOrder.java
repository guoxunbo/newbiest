package com.newbiest.mms.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * 发料单 指定具体批次进行发料
 * @author guoxunbo
 * @date 12/24/20 1:29 PM
 */

@Entity
@DiscriminatorValue(Document.CATEGORY_ISSUE_LOT)
public class IssueLotOrder extends Document {

    public static final String GENERATOR_ISSUE_LOT_ORDER_ID_RULE = "CreateIssueLotOrder";

}
