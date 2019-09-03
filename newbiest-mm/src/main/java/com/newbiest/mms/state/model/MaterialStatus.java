package com.newbiest.mms.state.model;

import com.newbiest.commom.sm.model.Status;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created by guoxunbo on 2019/2/12.
 */
@Entity
@DiscriminatorValue(MaterialStatusCategory.CATEGORY_MATERIAL)
public class MaterialStatus extends Status{

    public static final String STATUS_PACKED = "Packed";
    public static final String STATUS_WAIT = "Wait";
    public static final String STATUS_IN = "In";

}
