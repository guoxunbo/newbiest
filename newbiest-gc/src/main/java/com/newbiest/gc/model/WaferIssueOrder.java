package com.newbiest.gc.model;

import com.newbiest.mms.model.Document;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * 晶圆发料单
 */
@Entity
@DiscriminatorValue(WaferIssueOrder.CATEGORY_WAFER_ISSUE)
public class WaferIssueOrder extends Document {

    public static final String CATEGORY_WAFER_ISSUE = "WaferIssue";


}
