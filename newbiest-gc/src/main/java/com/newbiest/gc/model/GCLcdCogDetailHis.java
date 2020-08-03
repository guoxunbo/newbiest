package com.newbiest.gc.model;

import com.newbiest.base.model.NBHis;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * Created by guozhangLuo
 */
@Entity
@Table(name="GC_LCDCOG_DETAIL_HIS")
@Data
public class GCLcdCogDetailHis extends NBHis {

    private static final long serialVersionUID = -8075936261995774501L;

    /**
     * 外包装ID
     */
    @Column(name="BOXA_ID")
    private String boxaId;

    /**
     * 内包装ID
     */
    @Column(name="BOXB_ID")
    private String boxbId;

    /**
     * 数量
     */
    @Column(name="CHIP_QTY")
    private BigDecimal chipQty = BigDecimal.ZERO;

    /**
     * 等级
     */
    @Column(name="GRADE")
    private String grade;

    /**
     * 类型
     */
    @Column(name="BOX_TYPE")
    private String boxType;

    /**
     * 序号
     */
    @Column(name = "SERIAL_NUM")
    private String serialNum;

    /**
     * 仓库
     */
    @Column(name = "WAREHOUSE_ID")
    private String warehouseId;

    /**
     * 导入类型
     */
    @Column(name = "IMPORT_TYPE")
    private String importType;

    /**
     * 导入编码
     */
    @Column(name = "IMPORT_CODE")
    private String importCode;

}
