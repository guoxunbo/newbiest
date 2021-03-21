package com.newbiest.mms.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * 成品发料单
 */

@Entity
@DiscriminatorValue(Document.CATEGORY_ISSUE_FINISH_GOOD)
public class IssueFinishGoodOrder extends Document {

    public static final String GENERATOR_ISSUE_FINISH_GOOD_ORDER_ID_RULE = "createIssueFinishGoodOrder";

    @Override
    public String getCategory(){
        return Document.CATEGORY_ISSUE_FINISH_GOOD;
    }
}
