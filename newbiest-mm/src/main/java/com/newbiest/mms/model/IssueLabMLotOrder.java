package com.newbiest.mms.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


/**
 * 实验室物料 发料单
 */
@Entity
@DiscriminatorValue(Document.CATEGORY_ISSUE_LABMLOT)
public class IssueLabMLotOrder extends Document {

    public static final String GENERATOR_ISSUE_LABMLOT_ORDER_ID_RULE = "CreateIssueLabMLotOrder";

    @Override
    public String getCategory(){
        return Document.CATEGORY_ISSUE_LABMLOT;
    }
}
