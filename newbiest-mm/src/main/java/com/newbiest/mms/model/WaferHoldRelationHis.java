package com.newbiest.mms.model;

import com.newbiest.base.model.NBHis;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by guozhangLuo
 */
@Entity
@Table(name="GC_WAFER_HOLD_RELATION_HIS")
@Data
public class WaferHoldRelationHis extends NBHis {

    public static final String SCM_ADD = "SCMADD";
    public static final String SCM_DELETE = "SCMDELETE";
    public static final String HOLD_DELETE = "HoldDelete";

    /**
     * 晶圆ID
     */
    @Column(name="WAFER_ID")
    private String waferId;

    /**
     * Hold原因
     */
    @Column(name = "HOLD_REASON")
    private String holdReason;

    /**
     * 类型
     */
    @Column(name = "TYPE")
    private String type;
}
