package com.newbiest.gc.model;

import com.newbiest.base.model.NBUpdatable;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="GC_WAFER_HOLD_RELATION")
@Data
public class WaferHoldRelation extends NBUpdatable {

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
}
