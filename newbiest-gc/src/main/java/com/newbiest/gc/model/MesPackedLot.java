package com.newbiest.gc.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.newbiest.base.model.NBUpdatable;
import com.newbiest.base.utils.DateUtils;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Glaxycore 的Mes已经包装好的批次别名
 * Created by guoxunbo on 2019-08-20 16:12
 */
@Data
@Entity
@Table(name="MES_PACKED_LOT")
public class MesPackedLot implements Serializable {

    public static final String PACKED_STATUS_RECEIVED = "RECEIVED";
    public static final String PACKED_STATUS_IN = "IN";

    public static final String PRODUCT_CATEGORY_FT = "FT";
    public static final String PRODUCT_CATEGORY_WLFT = "WLFT";
    public static final String PRODUCT_CATEGORY_WLT = "WLT";
    public static final String PRODUCT_CATEGORY_CP = "CP";
    public static final String PRODUCT_CATEGORY_COM = "COM";
    public static final String PRODUCT_CATEGORY_LSP = "LSP";
    public static final String PRODUCT_CATEGORY_LCP = "LCP";
    public static final String PRODUCT_CATEGORY_SCP = "SCP";
    public static final String PRODUCT_CATEGORY_COB = "COB";

    public static final String REPLACE_FLAG = "true";

    public static final String IN_FLAG_ONE = "1";
    public static final String ZJ_SUB_NAME = "GCZJ";
    public static final String SH_SUB_NAME = "GCSH";
    public static final String ZH_WAREHOUSE = "8143";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "PACKED_LOT_RRN")
    private Long packedLotRrn;

    @Column(name = "BOX_ID")
    private String boxId;

    @Column(name = "PRODUCT_ID")
    private String productId;

    @Column(name = "WORKORDER_ID")
    private String workorderId;

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
    @JsonFormat(timezone = NBUpdatable.GMT_PE, pattern = DateUtils.DEFAULT_DATETIME_PATTERN)
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
     * 入库单号
     */
    @Column(name="SHIP_SEQUENCE_NUMBER")
    private String shipSequenceNumber;

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

    /**
     * 载具号
     */
    @Column(name="LOT_ID")
    private String lotId;

    /**
     * 物料批次号
     */
    @Column(name="MATERIAL_LOT_NAME")
    private String materialLotName;

    /**
     * 载具号
     */
    @Column(name="PCODE")
    private String pcode;

    /**
     * 供应商
     */
    @Transient
    private String subName;

    /**
     * FAB_DEVICE
     */
    @Transient
    private String fabDevice;

    /**
     * PRODUCT_TYPE
     */
    @Transient
    private String productType;

    /**
     * IMPORT_TYPE
     */
    @Transient
    private String importType;

    /**
     * 扫描序号
     */
    @Transient
    private String scanSeq;
}
