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
@Table(name="GC_PRODUCT_RELATION")
@Data
public class GCProductRelation extends NBUpdatable {

    private static final long serialVersionUID = -8075936261995774501L;

    public static final String GRADE_TYPE = "GRADE";
    public static final String SUBCODE_TYPE = "SUBCODE";

    /**
     * 产品型号
     */
    @Column(name="PRODUCT_ID")
    private String productId;

    /**
     * 等级或者二级代码
     */
    @Column(name="GRADE_SUBCODE")
    private String gradeSubcode;

    /**
     * 类型
     */
    @Column(name="TYPE")
    private String type;

}
