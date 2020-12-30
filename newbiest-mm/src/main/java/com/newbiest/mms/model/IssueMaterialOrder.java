package com.newbiest.mms.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * 发料单 指定物料名称不指定具体批次的发料单
 * @author guoxunbo
 */

@Entity
@DiscriminatorValue(Document.CATEGORY_ISSUE_MATERIAL)
public class IssueMaterialOrder extends Document {

    public static final String GENERATOR_ISSUE_MATERIAL_ORDER_ID_RULE = "CreateIssueMaterialOrder";

}
