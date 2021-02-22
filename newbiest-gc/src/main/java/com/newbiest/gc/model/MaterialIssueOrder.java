package com.newbiest.gc.model;

import com.newbiest.mms.model.Document;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * 原材料发料单
 */
@Entity
@DiscriminatorValue(MaterialIssueOrder.CATEGORY_WAFER_ISSUE)
public class MaterialIssueOrder extends Document {

    public static final String CATEGORY_WAFER_ISSUE = "MaterialIssue";


}
