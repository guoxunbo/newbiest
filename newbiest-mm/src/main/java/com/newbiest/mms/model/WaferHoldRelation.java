package com.newbiest.mms.model;

import com.newbiest.base.model.NBUpdatable;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="GC_WAFER_HOLD_RELATION")
@Data
public class WaferHoldRelation extends NBUpdatable {

    public static final String HOLD_TYPE_WLA = "WLA";
    public static final String HOLD_TYPE_SCM = "SCM";

    /**
     * 晶圆ID
     */
    @Column(name="WAFER_ID")
    private String waferId;

    /**
     * 保留原因
     */
    @Column(name = "HOLD_REASON")
    private String holdReason;

    /**
     * 类型
     */
    @Column(name = "TYPE")
    private String type = HOLD_TYPE_WLA;
}
