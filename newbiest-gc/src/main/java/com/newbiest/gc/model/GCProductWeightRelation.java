package com.newbiest.gc.model;

import com.newbiest.base.model.NBUpdatable;
import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * Created by guozhangLuo
 */
@Entity
@Table(name="GC_PRODUCT_WEIGHT_RELATION")
@Data
public class GCProductWeightRelation extends NBUpdatable {

    private static final long serialVersionUID = -8075936261995774501L;

    /**
     * 产品号
     */
    @Column(name="PRODUCT_ID")
    private String productId;

    /**
     * 整包颗数
     */
    @Column(name="PACKAGE_QTY")
    private BigDecimal packageQty = BigDecimal.ZERO;

    /**
     * 每盘芯片数
     */
    @Column(name="DISC_CHIP_QTY")
    private BigDecimal discChipQty = BigDecimal.ZERO;

    /**
     * 整箱包数
     */
    @Column(name="BOX_PACKED_QTY")
    private BigDecimal boxPackedQty = BigDecimal.ZERO;

    /**
     * 最小包数
     */
    @Column(name="MIN_PACKED_QTY")
    private BigDecimal minPackedQty = BigDecimal.ZERO;

    /**
     * 最大包数
     */
    @Column(name="MAX_PACKED_QTY")
    private BigDecimal maxPackedQty = BigDecimal.ZERO;

    /**
     * 箱重量
     */
    @Column(name="BOX_WEIGHT")
    private BigDecimal boxWeight = BigDecimal.ZERO;

    /**
     * 盘重量
     */
    @Column(name="DISC_WEIGHT")
    private BigDecimal discWeight = BigDecimal.ZERO;

    /**
     * 盖重量
     */
    @Column(name="COVER_WEIGHT")
    private BigDecimal coverWeight = BigDecimal.ZERO;

    /**
     * 管夹重量
     */
    @Column(name="CLIP_WEIGHT")
    private BigDecimal clipWeight = BigDecimal.ZERO;

    /**
     * 整包芯片重量
     */
    @Column(name="PACKAGE_CHIP_WEIGHT")
    private BigDecimal packageChipWeight = BigDecimal.ZERO;

    /**
     * 浮动值
     */
    @Column(name="FLOAT_QTY")
    private BigDecimal floatQty = BigDecimal.ZERO;

}
