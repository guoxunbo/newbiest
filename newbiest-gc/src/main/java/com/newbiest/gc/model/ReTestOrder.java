package com.newbiest.gc.model;

import com.newbiest.mms.model.Document;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created by guoxunbo on 2019-08-30 16:08
 */
@Entity
@DiscriminatorValue(ReTestOrder.CATEGORY_RETEST)
public class ReTestOrder extends Document {

    public static final String CATEGORY_RETEST = "ReTest";


}
