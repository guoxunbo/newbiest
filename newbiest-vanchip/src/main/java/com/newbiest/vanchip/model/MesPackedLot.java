package com.newbiest.vanchip.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * VanChip的Mes已经包装好的批次别名
 */
@Data
@Entity
@Table(name="MES_PACKED_LOT")
public class MesPackedLot implements Serializable {

    @Id
    @Column(name = "PACKED_LOT_RRN")
    private Long packedLotRrn;

    @Column(name = "BOX_ID")
    private String boxId;

    @Column(name = "PRODUCT_ID")
    private String productId;

    @Column(name = "WORKORDER_ID")
    private String workOrderId;

    @Column(name = "LEVEL_TWO_CODE")
    private String levelTwoCode;

    @Column(name = "WAFER_ID")
    private String waferId;

    @Column(name = "GRADE")
    private String grade;

    /**
     * 包装数量
     */
    @Column(name = "QUANTITY")
    private Integer quantity;

    @Column(name = "FINAL_OPERATION_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date finalOperationTime;

    /**
     * 销售备注
     */
    @Column(name = "SALES_NOTE")
    private String salesNote;

    @Column(name = "PARENT_RRN")
    private Long parentRrn;

    @Column(name = "PACKED_STATUS")
    private String packedStatus;

    @Column(name = "TYPE")
    private String type;

    /**
     * 入库备注
     */
    @Column(name = "TREASURY_NOTE")
    private String treasuryNote;

    @Column(name = "PRODUCTION_NOTE")
    private String productionNote;

    /**
     * 标准数量
     */
    @Column(name = "STANDARD_QTY")
    private Integer standardQty;

    /**
     * 保税属性
     */
    @Column(name="BONDED_PROPERTY")
    private String bondedProperty;

    /**
     * 产品分类
     */
    @Column(name="PRODUCT_Category")
    private String productCategory;

    /**
     * 入库序号
     */
    @Column(name="SHIP_SERIAL_NUMBER")
    private String shipSerialNumber;

    /**
     * 仓库号
     */
    @Column(name="STOCK")
    private String stock;

    @Column(name="PACKAGE_CHECK_COMMENT")
    private String packageCheckComment;

    @Column(name="VACUUM_CHECK_COMMENT")
    private String vacuumCheckComment;

    @Column(name="LOCATION")
    private String location;

    @Column(name="ERP_PRODUCT_ID")
    private String erpProductId;

    @Column(name="CST_ID")
    private String cstId;

    @Column(name="WAFER_QTY")
    private Integer waferQty;


    @Column(name="WAFER_MARK")
    private String waferMark;

    @Column(name="IN_FLAG")
    private String inFlag;

    /**
     * 打印型号
     */
    @Column(name="PRINT_MODEL_ID")
    private String printModelId;

    /**
     * 是否替换
     */
    @Column(name="REPLACE_FLAG")
    private String replaceFlag;

}
