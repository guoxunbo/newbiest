package com.newbiest.mms.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * 内部发料单 指定具体批次进行发料
 */
@Entity
@DiscriminatorValue(Document.CATEGORY_ISSUE_MLOT)
public class IssueMLotOrder extends Document {

    public static final String GENERATOR_ISSUE_MLOT_ORDER_ID_RULE = "CreateIssueMLotOrder";

    @Override
    public String getCategory(){
        return Document.CATEGORY_ISSUE_MLOT;
    }
}
