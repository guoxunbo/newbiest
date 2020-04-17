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
@Table(name="GC_PRODUCT_SUBCODE_SET")
@Data
public class GCProductSubcode extends NBUpdatable {

    private static final long serialVersionUID = -8075936261995774501L;

    /**
     * 产品型号
     */
    @Column(name="PRODUCT_ID")
    private String productId;

    /**
     * 二级代码
     */
    @Column(name="SUBCODE")
    private String subcode;

}
