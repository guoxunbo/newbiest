package com.newbiest.gc.model;

import com.newbiest.mms.model.Document;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * FT重测发料单
 */
@Entity
@DiscriminatorValue(FtRetestOrder.CATEGORY_FT_RETEST)
public class FtRetestOrder extends Document {

    public static final String CATEGORY_FT_RETEST = "FtRetest";


}
