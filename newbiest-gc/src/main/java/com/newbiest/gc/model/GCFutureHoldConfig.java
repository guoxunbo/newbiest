package com.newbiest.gc.model;

import com.newbiest.base.model.NBUpdatable;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by wangxinqi
 */
@Entity
@Table(name="GC_FUTURE_HOLD_CONFIG")
@Data
public class GCFutureHoldConfig extends NBUpdatable {

    /**
     * LotId
     */
    @Column(name = "LOT_ID")
    private String lotId;

    /**
     * HOLD原因
     */
    @Column(name="HOLD_REASON")
    private String holdReason;
}
