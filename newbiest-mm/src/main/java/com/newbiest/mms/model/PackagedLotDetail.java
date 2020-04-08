package com.newbiest.mms.model;

import com.newbiest.base.model.NBBase;
import com.newbiest.base.model.NBUpdatable;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "MMS_PACKAGED_LOT_DETAIL")
@Data
public class PackagedLotDetail extends NBBase {

    /**
     * 包装批次主键
     */
    @Column(name="PACKAGED_LOT_RRN")
    private Long packagedLotRrn;

    /**
     * 包装批次号
     */
    @Column(name="PACKAGED_LOT_ID")
    private String packagedLotId;

    /**
     * 被包装批次的主键
     */
    @Column(name="MATERIAL_LOT_RRN")
    private Long materialLotRrn;

    /**
     * 被包装批次的批次号
     */
    @Column(name="MATERIAL_LOT_ID")
    private String materialLotId;

    /**
     * 包装数量
     */
    @Column(name="QTY")
    private BigDecimal qty = BigDecimal.ZERO;

    /**
     * 载具号
     */
    @Column(name="LOT_ID")
    private String lotId;

}
