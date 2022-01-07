package com.newbiest.mms.model;

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
public class FutureHoldConfig extends NBUpdatable {

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

    /**
     * 产品类别
     */
    @Column(name = "PRODUCT_AREA")
    private String productArea;

    /**
     * 接收来源
     */
    @Column(name = "RECEIVE_TYPE")
    private String receiveType;
}
