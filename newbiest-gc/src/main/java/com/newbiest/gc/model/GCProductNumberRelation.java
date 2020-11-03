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
@Table(name="GC_PRODUCT_NUMBER_RELATION")
@Data
public class GCProductNumberRelation extends NBUpdatable {

    private static final long serialVersionUID = -8075936261995774501L;

    /**
     * 产品号
     */
    @Column(name="PRODUCT_ID")
    private String productId;

    /**
     * 整包数
     */
    @Column(name="PACKAGE_QTY")
    private BigDecimal packageQty = BigDecimal.ZERO;

    /**
     * 整箱包数
     */
    @Column(name="BOX_PACKED_QTY")
    private BigDecimal boxPackedQty = BigDecimal.ZERO;

    /**
     * 总颗数
     */
    @Column(name="TOTAL_NUMBER")
    private BigDecimal totalNumber = BigDecimal.ZERO;

}
