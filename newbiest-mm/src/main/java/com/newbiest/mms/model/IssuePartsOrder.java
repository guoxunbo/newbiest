package com.newbiest.mms.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 *备品备件发料
 */
@Entity
@DiscriminatorValue(Document.CATEGORY_ISSUE_PARTS)
public class IssuePartsOrder extends Document {

    public static final String GENERATOR_ISSUE_PARTS_ORDER_ID_RULE = "CreateIssuePartsOrder";

    @Override
    public String getCategory(){
        return Document.CATEGORY_ISSUE_PARTS;
    }
}
