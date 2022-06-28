package com.newbiest.gc.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.collect.Lists;
import com.newbiest.base.model.NBUpdatable;
import com.newbiest.base.utils.DateUtils;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
    public static final String PRODUCT_CATEGORY_RW = "RW";
    public static final String PRODUCT_CATEGORY_WLT = "WLT";
    public static final String PRODUCT_CATEGORY_CP = "CP";
    public static final String PRODUCT_CATEGORY_COM = "COM";
    public static final String PRODUCT_CATEGORY_LSP = "LSP";
    public static final String PRODUCT_CATEGORY_LCP = "LCP";
    public static final String PRODUCT_CATEGORY_SCP = "SCP";
    public static final String PRODUCT_CATEGORY_COB = "COB";
    public static final List<String> VBOX_CATEGORY_LIST = Lists.newArrayList(PRODUCT_CATEGORY_FT, PRODUCT_CATEGORY_COM, PRODUCT_CATEGORY_COB);

    public static final String REPLACE_FLAG = "true";

    public static final String PACKED_TYPE = "VBOX";
    public static final String PACKED_TYPE_CPCST_PREIN = "CPCST_PREIN";
    public static final String LEVEL_TWO_CODE_FFFFF = "FFFFF";

    public static final String STORAGE_ID_SH = "HJ AZ5000";
    public static final String STORAGE_ID_ZSH = "ZHJ AZ6000";

    public static final String GRADE_F3 = "F3";

    public static final String IN_FLAG_ONE = "1";
    public static final String ZJ_SUB_NAME = "GCZJ";
    public static final String SH_SUB_NAME = "GCSH";
    public static final String ZH_WAREHOUSE = "8143";
    public static final String STRING_LINE = "|";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "PACKED_LOT_RRN")
    private Long packedLotRrn;

    @Column(name = "BOX_ID")
    private String boxId;

    @Column(name = "CREATED")
    private Date created;

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
     * 原产品型号
     */
    @Column(name="ORG_PRODUCT_ID")
    private String orgProductId;

    /**
     * 原产品等级
     */
    @Column(name="BIN_TYPE")
    private String binType;

    /**
     * 载具号
     */
    @Column(name="PCODE")
    private String pcode;

    /**
     * 预入前工单号
     */
    @Column(name="SOURCE_WORKORDER_ID")
    private String sourceWorkorderId;

    /**
     * WLA测试位
     */
    @Column(name="WLA_TEST_BIT")
    private String wlaTestBit;

    /**
     * WLA程序位
     */
    @Column(name="PROGRAM_BIT")
    private String programBit;

    /**
     * 内批号
     */
    @Column(name="ASSY_LOT_NO")
    private String assyLotNo;

    /**
     * 供应商
     */
    @Column(name="VENDER")
    private String vender;

    /**
     * 原Lot昊
     */
    @Column(name = "SOURCE_LOT_ID")
    private String sourceLotId;

    /**
     * 产品分类
     */
    @Column(name = "PRODUCT_TYPE")
    private String productType;

    /**
     * 发料产品型号
     */
    @Column(name = "MATERIAL_NAME")
    private String materialName;

    /**
     * 来料发料等级
     */
    @Column(name = "ISSUE_GRADE")
    private String issueGrade;

    /**
     * 来料发料二级代码
     */
    @Column(name = "SUBCODE")
    private String subcode;

    /**
     * 来料发料保税属性
     */
    @Column(name = "ISSUE_LOCATION")
    private String issueLocation;

    /**
     * 来料发料类别
     */
    @Column(name = "CATEGORY")
    private String category;

    /**
     * 来料发料仓库
     */
    @Column(name = "WAREHOUSE_ID")
    private String warehouseId;

    /**
     * 来料发料reserved22
     */
    @Column(name = "SUB_NAME")
    private String subName;

    /**
     * 来料发料reserved23
     */
    @Column(name = "SHIP_TO")
    private String shipTo;

    /**
     * 来料发料reserved24
     */
    @Column(name = "FAB_DEVICE")
    private String fabDevice;

    /**
     * 来料发料reserved25
     */
    @Column(name = "LOT_TYPE")
    private String lotType;

    /**
     * 来料发料reserved49
     */
    @Column(name = "TEST_SOURCE")
    private String testSource;

    /**
     * 来料发料reserved50
     */
    @Column(name = "WAFER_SOURCE")
    private String waferSource;

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
