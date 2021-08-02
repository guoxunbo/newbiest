package com.newbiest.mms.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


/**
 * 指定物料发料
 */
@Entity
@DiscriminatorValue(Document.CATEGORY_ISSUE_BY_MATERIAL)
public class IssueByMaterialOrder extends Document {

    public static final String GENERATOR_ISSUE_BY_MATERIAL_ORDER_ID_RULE = "CreateIssueByMaterialOrder";

    @Override
    public String getCategory(){
        return Document.CATEGORY_ISSUE_BY_MATERIAL;
    }
}
