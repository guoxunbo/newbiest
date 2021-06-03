package com.newbiest.mms.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


/**
 * 指定批次发料和数量
 */
@Entity
@DiscriminatorValue(Document.CATEGORY_ISSUE_BY_MLOT)
public class IssueByMLotOrder extends Document {

    public static final String GENERATOR_ISSUE_BY_MLOT_ORDER_ID_RULE = "CreateIssueByMLotOrder";

    @Override
    public String getCategory(){
        return Document.CATEGORY_ISSUE_BY_MLOT;
    }
}
