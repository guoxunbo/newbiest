package com.newbiest.gc.model;

import com.newbiest.base.model.NBUpdatable;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by guozhangLuo
 */
@Entity
@Table(name="GC_PRODUCT_MODEL_CONVERSION")
@Data
public class GCProductModelConversion extends NBUpdatable {

    private static final long serialVersionUID = -8075936261995774501L;

    /**
     * 产品型号
     */
    @Column(name="PRODUCT_ID")
    private String productId;

    /**
     * 转换型号
     */
    @Column(name="CONVERSION_MODEL_ID")
    private String conversionModelId;

    /**
     * 产品型号类别
     */
    @Column(name="MODEL_CATEGORY")
    private String modelCategory;

}
