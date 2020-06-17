package com.newbiest.gc.model;

import com.newbiest.mms.model.Document;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * 其他发料单
 */
@Entity
@DiscriminatorValue(OtherIssueOrder.CATEGORY_WAFER_ISSUEA)
public class OtherIssueOrder extends Document {

    public static final String CATEGORY_WAFER_ISSUEA = "WaferIssueA";


}
