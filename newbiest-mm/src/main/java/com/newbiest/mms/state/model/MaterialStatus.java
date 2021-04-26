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

    public static final String STATUS_CREATE = "Create";

    public static final String STATUS_IQC = "IQC";
    public static final String STATUS_RECEIVE = "Receive";

    public static final String STATUS_PACKED = "Packed";
    public static final String STATUS_WAIT = "Wait";
    public static final String STATUS_IN = "In";
    public static final String STATUS_RESERVED = "Reserved";

    public static final String STATUS_SPLIT = "SPLIT";
    public static final String STATUS_MERGED = "MERGED";

    public static final String STATUS_OK = "OK";
    public static final String STATUS_NG = "NG";

    public static final String STATUS_PACKAGE= "Package";

    public static final String STATUS_PACK_CHECK= "PackCkeck";

    public static final String STATUS_ISSUE= "Issue";


}
