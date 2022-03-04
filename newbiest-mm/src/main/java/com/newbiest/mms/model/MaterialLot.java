package com.newbiest.mms.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.collect.Lists;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.model.NBUpdatable;
import com.newbiest.base.utils.DateUtils;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.base.utils.ThreadLocalContext;
import com.newbiest.commom.sm.model.StatusLifeCycle;
import com.newbiest.mms.exception.MmsException;
import com.newbiest.mms.state.model.MaterialStatus;
import com.newbiest.mms.state.model.MaterialStatusCategory;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by guoxunbo on 2019/2/26.
 */
@Entity
@Table(name="MMS_MATERIAL_LOT")
@Data
public class MaterialLot extends NBUpdatable implements StatusLifeCycle{

    public static final String DEFAULT_REVERSE_DATA_PATTERN = "dd/MM/yyyy";
    public static final String DEFAULT_NO_FORMAT_DATE_PATTERN = "yyyyMMdd";
    public static final String DEFAULT_NOT_S_DATE_PATTERN = "yyyy-MM-dd HH:mm";
    public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * 产品分类
     */
    public static final String PRODUCT_TYPE_PROD = "PROD";

    /**
     * 生成物料批次号的规则
     */
    public static final String GENERATOR_MATERIAL_LOT_ID_RULE = "CreateMLot";

    /**
     * 生成多箱称重事物号规则
     */
    public static final String GENERATOR_MATERIAL_LOT_WEIGHT_RULE = "BoxsWeight";

    /**
     * 生成多箱称重事物号规则
     */
    public static final String GENERATOR_RAW_MATERIAL_SPARE_RULE = "RawMaterialSpare";

    /**
     * 生成物料子批号的规则
     */
    public static final String GENERATOR_SUB_MATERIAL_LOT_ID_RULE = "CreateSubMLot";

    /**
     * 生成来料导入的导入编码
     */
    public static final String GENERATOR_INCOMING_MLOT_IMPORT_CODE_RULE = "ImcomingMLotImport";

    /**
     * 生成二维码标签打印流水
     */
    public static final String GENERATOR_QRCODE_LABEL_PRINT_SEQ_RULE = "GCQRCodePrintSeq";

    /**
     * 箱子称重流水号
     */
    public static final String GENERATOR_QRCODE_LABEL_WEIGHT_SEQ_RULE = "BoxWeightSeq";

    /**
     * RW辅料Tape箱号生成规则
     */
    public static final String TAPE_MATERIAL_LOT_ID_RULE = "CreateTapeLotId";

    /**
     * wafer拆箱的lotId生成规则
     */
    public static final String CREATE_WAFER_LOT_ID_RULE = "CreateWaferLotId";

    /**
     * wafer拆箱的cstId生成规则
     */
    public static final String CREATE_WAFER_CST_ID_RULE = "CreateWaferCstId";

    /**
     * 手动快递下单
     */
    public static final String PLAN_ORDER_TYPE_MANUAL  = "ManualOrder";

    /**
     * 自动快递下单
     */
    public static final String PLAN_ORDER_TYPE_AUTO  = "AutoOrder";

    public static final String TRANSTYPE_BIND_WORKORDER = "bindWorkorder";

    public static final String TRANSTYPE_UN_BIND_WORKORDER = "unbindWorkorder";

    /**
     * F等级
     */
    public static final String GEADE_F = "F";

    /**
     * COB装箱规则
     */
    public static final String COB_PACKCASE = "COBPackCase";

    /**
     * RW装箱规则
     */
    public static final String RW_PACKCASE = "CSTPackCase";

    /**
     * WLT/CP出货物料批次验证规则
     */
    public static final String WLT_SHIP_MLOT_MERGE_RULE = "WltCPShipCase";

    public static final String RECEIVE_ERROR = "ERROR";

    /**
     * COB一个lot最大片数为13
     */
    public static final Integer COB_UNIT_SIZE = 13;

    /**
     * 物料批次出货、发料、接收、重测与单据的验证规则名称
     */
    public static final String MLOT_SHIP_DOC_VALIDATE_RULE_ID = "MLotShipDocRule";  //COM出货单据验证规则
    public static final String WLT_SHIP_DOC_VALIDATE_RULE_ID = "WLTStockOutDocRule";    //WLT出货单据验证
    public static final String MLOT_RESERVED_DOC_VALIDATE_RULE_ID = "MLotReservedRule"; //备货单据验证规则
    public static final String MLOT_RETEST_DOC_VALIDATE_RULE_ID = "MLotReTestRule"; //物料重测发料单据验证规则
    public static final String WAFER_RECEIVE_DOC_VALIDATE_RULE_ID = "WaferReceiveDocLineRule";  //晶圆接收单据验证规则
    public static final String COB_WAFER_RECEIVE_DOC_VALIDATE_RULE_ID = "COBWaferReceiveDocLineRule"; //COB晶圆接收单据验证规则
    public static final String WAFER_ISSUE_DOC_VALIDATE_RULE_ID = "WaferIssueDocLineRule";  //晶圆发料验证规则
    public static final String COB_WAFER_ISSUE_DOC_VALIDATE_RULE_ID = "COBWaferIssueDocLineRule";   //COB晶圆发料单据验证规则
    public static final String FT_STOCK_OUT_DOC_VALIDATE_RULE_ID = "FTStockOutDocRule"; //FT出货单据验证规则
    public static final String COG_MLOT_RECEIVE_DOC_VALIDATE_RULE_ID = "CogMLotReceiveDocRule"; //COG来料接受单据验证规则
    public static final String RAW_MATERIAL_ISSUE_DOC_VALIDATE_RULE_ID = "RawMaterialIssueDocRule";  //原材料发料单据验证规则
    public static final String MLOT_THREESIDE_DOC_VALIDATE_RULE_ID = "MLotThreeSideDocRule";  //三方销售单据验证规则
    public static final String RW_MLOT_STOCK_OUT_DOC_VALIDATE_RULE_ID = "RwMLotStockOutDocRule";  //RW出货单据验证规则
    public static final String RW_MLOT_SCRAP_AND_SHIP_VALIDATE_RULE_ID = "RwMaterialScrapShipDocRule";  //原材料报废出库单据验证规则
    public static final String WLT_OTHER_STOCK_OUT_RULE_ID = "WltOtherStockOutRule";  //WLT/CP其它出单据验证规则
    public static final String FT_RETEST_DOC_VALIDATE_RULE_ID = "FtVboxReTestRule"; //FT真空包重测发料单据验证规则

    public static final String MOBILE_RAW_ISSUE_WHERE_CLAUSE="GCRawMaterialIssueOrder";
    public static final String MOBILE_RETEST_WHERE_CLAUSE = "GCReTestManager";
    public static final String MOBILE_WLT_OR_CP_STOCK_OUT_ORDER_WHERE_CLAUSE = "GCWltOrCpStockOutOrder";
    public static final String MOBILE_COM_WAFER_ISSUE_MANAGER_WHERE_CLAUSE = "GCCOMWaferIssueManager";
    public static final String GC_SCM_LOT_QUERY_WHERE_CLAUSE = "GCScmLotQuery";
    public static final String GC_INCOMING_MATERIAL_IMPORT = "GCIncomingMaterialImport"; //来料导入
    public static final String WLT_PACKAGED_LOT_SCAN = "WLTPackagedLotScan"; //LOT产线入库

    /**
     * 香港仓依订单出货
     */
    public static final String HKWAREHOUSE_BY_ORDER_STOCK_OUT_RULE_ID = "HKByOrderStockOutRule";

    public static final String MM_PACKED_LOTIN_FLAG  = "1";

    public static final String CATEGORY_UNIT = "Unit";
    public static final String CATEGORY_LOT = "Lot";

    public static final String HOLD_STATE_ON = "On";
    public static final String HOLD_STATE_OFF = "Off";

    public static final String STATUS_STOCK = "Stock";

    public static final String STATUS_HOLD = "Hold";

    public static final String STATUS_FIN = "Fin";

    public static final String STATUS_OQC = "OQC";
    public static final String STATUS_IN = "In";
    public static final String STATUS_OK = "OK";
    public static final String STATUS_CREATE = "Create";

    public static final String CATEGORY_PACKAGE = "Package";

    public static final String PACKAGE_TYPE = "PackCase";

    public static final String INCOMING_MLOT_IMPORTTYPE = "GCMaterialLotImportType";

    public static final String BONDED_PROPERTY_LIST = "GCBondedPropertyList";

    public static final String GC_RAW_MATERIAL_WAIT_ISSUE_MLOT = "GCRawMaterialWaitIssueMLot";

    public static final String PRINT_CHECK = "check";
    public static final String PRINT_DATE_PATTERN = "yyMMdd";
    public static final String PRODUCT_CATEGORY = "COM";
    public static final String GC_CODE = "0000110461";

    public static final String IMPORT_SENSOR_CP = "SENSOR_CP";
    public static final String IMPORT_LCD_CP = "LCD_CP";
    public static final String IMPORT_WLA = "WLA";
    public static final String IMPORT_WLT = "WLT";
    public static final String IMPORT_SENSOR = "SENSOR";
    public static final String IMPORT_RMA = "RMA";
    public static final String IMPORT_RETURN = "RETURN";
    public static final String IMPORT_CRMA = "CRMA";
    public static final String IMPORT_COB = "COB";
    public static final String IMPORT_COG = "COG";
    public static final String IMPORT_SOC = "SOC";
    public static final String IMPORT_FT = "FT";
    public static final String IMPORT_MASK = "MASK";

    public static final String RETEST_TYPE_COM = "COMReTest";
    public static final String RETEST_TYPE_FT = "FtReTest";

    /**
     * Wafer Source
     */
    public static final String RW_TO_CP_WAFER_SOURCE = "21";
    public static final String RW_WAFER_SOURCE = "20";
    public static final String COM_WAFER_SOURCE = "19";
    public static final String WLT_WAFER_SOURCE = "6";
    public static final String LCP_WAFER_SOURCE = "4";
    public static final String SCP_WAFER_SOURCE = "2";
    public static final String FT_WAFER_SOURCE = "10";
    public static final String FT_COB_WAFER_SOURCE = "16";
    public static final String WLFT_WAFER_SOURCE = "8";
    public static final String WLA_WAFER_SOURCE = "5";
    public static final String SENSOR_WAFER_SOURCE = "9";
    public static final String COB_WAFER_SOURCE = "16";
    public static final String SOC_WAFER_SOURCE = "18";
    public static final String WLT_PACK_RETURN_WAFER_SOURCE = "7";
    public static final String WLT_IN_FLAG_WAFER_SOURCE = "5";
    public static final String LCP_IN_FLAG_WAFER_SOURCE = "3";
    public static final String SCP_IN_FLAG_WAFER_SOURCE = "1";
    public static final String RAW_MATERIAL_WAFER_SOURCE = "60";
    public static final String CP_CHANGGE_RW_WAFER_SOURCE = "21";
    public static final String SOC_WAFER_SOURCE_UNMEASUREN = "13";
    public static final String SOC_WAFER_SOURCE_MEASURE = "14";
    public static final String MASK_WAFER_SOURCE = "99";

    /**
     * 根据产品结尾数字获取WaferSource
     */
    public static final String WAFER_SOURCE_END1 = "1";
    public static final String WAFER_SOURCE_END2 = "2";
    public static final String WAFER_SOURCE_END3 = "3";
    public static final String WAFER_SOURCE_END4 = "4";
    public static final String ERROR_WAFER_SOUCE = "E";

    /**
     * Reserved7
     */
    public static final String WLT_IN_FLAG_PRODUCTCATEGORY = "WLA0";
    public static final String CP_IN_FLAG_PRODUCTCATEGORY = "CP0";

    /**
     * 加密二级代码
     */
    public static final String GEADE_TA = "TA";
    public static final String GEADE_HA = "HA";
    public static final String GEADE_HA1 = "HA1";
    public static final String GEADE_SA = "SA";
    public static final String GEADE_MA = "MA";
    public static final String GEADE_WA = "WA";
    public static final String GEADE_HA2 = "HA2";
    public static final String GEADE_HA3 = "HA3";
    public static final String GEADE_NA = "NA";
    public static final String GEADE_DA = "DA";
    public static final String GEADE_EA = "EA";
    public static final String GEADE_AA = "AA";
    public static final String GEADE_A = "A";

    /**
     * 加密等级中的固定字符
     */
    public static final String GRADE_FIRST = "A";
    public static final String GRADE_FIXED_CHAR_ZERO = "0";
    public static final String GRADE_FIXED_CHAR_ONE = "1";
    public static final String GRADE_FIXED_CHAR_TWO = "2";
    public static final String GRADE_FIXED_CHAR_THREE = "3";
    public static final String GRADE_FIXED_CHAR_FOUR = "4";
    public static final String GRADE_FIXED_CHAR_Q = "Q";
    public static final String GRADE_FIXED_CHAR_Z = "Z";
    public static final String GRADE_UNDEFINED = "UNDEFINED";

    /**
     * 出货形态
     */
    public static final String STOCKOUT_TYPE_35 = "-3.5";
    public static final String STOCKOUT_TYPE_4 = "-4";

    public static final String ZJ_STOCK = "601";
    public static final String SH_STOCK = "400";
    public static final String HK_STOCK = "300";

    public static final String ZJ_WAREHOUSE = "8143";
    public static final String SH_WAREHOUSE = "8142";
    public static final String HK_WAREHOUSE = "8150";

    public static final String WAREHOUSE_SH = "SH_STOCK";
    public static final String WAREHOUSE_ZJ = "ZJ_STOCK";
    public static final String WAREHOUSE_HK = "HK_STOCK";

    public static final String LOCATION_SH = "SH";
    public static final String BONDED_PROPERTY_ZSH = "ZSH";

    public static final List<String> BONDED_LIST = Lists.newArrayList("SWJF", "SWKY", "SWHT", "WJF", "WKY", "WHT");

    public static final String MLOT_SBB = "SBB";
    public static final String MLOT_SBC = "SBC";

    /**
     * 物料批次号
     */
    @Column(name="MATERIAL_LOT_ID")
    private String materialLotId;

    /**
     * 等级
     */
    @Column(name="GRADE")
    private String grade;

    /**
     * 状态模型主键
     * 从物料上携带
     */
    @Column(name="STATUS_MODEL_RRN")
    private Long statusModelRrn;

    /**
     * 状态大类
     */
    @Column(name="STATUS_CATEGORY")
    private String statusCategory;

    /**
     * 状态
     */
    @Column(name="STATUS")
    private String status;

    /**
     * 前置状态大类
     */
    @Column(name="PRE_STATUS_CATEGORY")
    private String preStatusCategory;

    /**
     * 前置状态
     */
    @Column(name="PRE_STATUS")
    private String preStatus;

    /**
     * Hold状态
     */
    @Column(name="HOLD_STATE")
    private String holdState = HOLD_STATE_OFF;

    /**
     * 批次接收数量
     * 只做记录使用不做任何运算
     */
    @Column(name="RECEIVE_QTY")
    private BigDecimal receiveQty = BigDecimal.ZERO;

    /**
     * 当前数量
     */
    @Column(name="CURRENT_QTY")
    private BigDecimal currentQty = BigDecimal.ZERO;

    /**
     * die数量
     */
    @Column(name="CURRENT_SUB_QTY")
    private BigDecimal currentSubQty = BigDecimal.ZERO;

    /**
     * 预留数量
     */
    @Column(name="RESERVED_QTY")
    private BigDecimal reservedQty = BigDecimal.ZERO;

    /**
     * 如果是子批，对应的母批的批号
     */
    @Column(name="PARENT_MATERIAL_LOT_ID")
    private String parentMaterialLotId;

    /**
     * 如果是子批，对应的母批的主键
     */
    @Column(name="PARENT_MATERIAL_LOT_RRN")
    private Long parentMaterialLotRrn;

    /**
     * 是否是子批的标记
     */
    @Column(name="SUB_MATERIAL_LOT_FLAG")
    private String subMaterialLotFlag;

    /**
     * 物料批次类别，用来区分是普通物料批次/物料单元批次
     */
    @Column(name="CATEGORY")
    private String category;

    /**
     * 包装类型 用于追加包装的时候进行验证
     */
    @Column(name="PACKAGE_TYPE")
    private String packageType;

    /**
     * 工单号
     */
    @Column(name="WORK_ORDER_ID")
    private String workOrderId;

    /**
     * 工单计划投入日期
     */
    @Column(name="WORK_ORDER_PLANPUT_TIME")
    private String workOrderPlanputTime;

    /**
     * 物料主键
     */
    @Column(name="MATERIAL_RRN")
    private Long materialRrn;

    /**
     * 物料名称
     */
    @Column(name="MATERIAL_NAME")
    private String materialName;

    /**
     * 物料版本
     */
    @Column(name="MATERIAL_VERSION")
    private Long materialVersion;

    /**
     * 物料描述
     */
    @Column(name="MATERIAL_DESC")
    private String materialDesc;

    /**
     * 物料类别
     */
    @Column(name="MATERIAL_CATEGORY")
    private String materialCategory;

    /**
     * 物料类型
     */
    @Column(name="MATERIAL_TYPE")
    private String materialType;

    /**
     * 库存单位
     */
    @Column(name="STORE_UOM")
    private String storeUom;

    /**
     * 接收日期
     */
    @Column(name="RECEIVE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(timezone = GMT_PE,pattern = DateUtils.DEFAULT_DATETIME_PATTERN)
    private Date receiveDate;

    /**
     * 有效时长
     */
    @Column(name="EFFECTIVE_LIFE")
    private Long effectiveLife;

    /**
     * 警告时长
     * 当达到此时长的时候触发警告
     */
    @Column(name="WARNING_LIFE")
    private Long warningLife;

    /**
     * 有效时长单位
     */
    @Column(name="EFFECTIVE_UNIT")
    private String effectiveUnit;

    @Column(name="DURABLE")
    private String durable;

    /**
     * 供应商
     */
    @Column(name="SUPPLIER")
    private String supplier;

    /**
     * 出货商
     */
    @Column(name="SHIPPER")
    private String shipper;

    /**
     * 快递单号
     */
    @Column(name="EXPRESS_NUMBER")
    private String expressNumber;

    /**
     * 快递公司
     */
    @Column(name="EXPRESS_COMPANY")
    private String expressCompany;

    /**
     * 下单类型
     */
    @Column(name="PLAN_ORDER_TYPE")
    private String planOrderType;

    /**
     * 载具号 aliasId
     */
    @Column(name="LOT_ID")
    private String lotId;

    /**
     * 产品分类 PROD/ENG
     */
    @Column(name="PRODUCT_TYPE")
    private String productType = PRODUCT_TYPE_PROD;

    /**
     * 原产品号
     */
    @Column(name="SOURCE_PRODUCT_ID")
    private String sourceProductId;

    /**
     * ERP备货的单据日期
     */
    @Column(name="DOC_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(timezone = GMT_PE,pattern = DateUtils.DEFAULT_DATETIME_PATTERN)
    private Date docDate;

    /**
     * 箱称重流水号
     */
    @Column(name="WEIGHT_SEQ")
    private String weightSeq;

    /**
     * 真空包二维码信息
     */
    @Column(name="VBOX_QRCODE_INFO")
    private String vboxQrcodeInfo;

    /**
     * 箱号二维码信息
     */
    @Column(name="BOX_QRCODE_INFO")
    private String boxQrcodeInfo;

    /**
     * 物料编码
     */
    @Column(name="MATERIAL_CODE")
    private String materialCode;

    /**
     * 三方销售单
     */
    @Column(name="THREE_SIDE_ORDER")
    private String threeSideOrder;

    /**
     * HOLD原因
     */
    @Column(name="HOLD_REASON")
    private String holdReason;

    /**
     * RW生成的内批号
     */
    @Column(name="INNER_LOT_ID")
    private String innerLotId;

    /**
     * RW产线入库时的LotId
     */
    @Column(name="LOT_CST")
    private String lotCst;

    /**
     * 膜厚
     */
    @Column(name="PCODE")
    private String pcode;

    /**
     * 客户标识
     */
    @Column(name="CUSTOMER_ID")
    private String customerId;

    /**
     * 原材料生产日期
     */
    @Column(name="MFG_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(timezone = GMT_PE,pattern = DateUtils.DEFAULT_DATETIME_PATTERN)
    private Date mfgDate;

    /**
     * 原材料有效日期
     */
    @Column(name="EXP_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(timezone = GMT_PE,pattern = DateUtils.DEFAULT_DATETIME_PATTERN)
    private Date expDate;

    /**
     * 最小原材料有效日期
     */
    @Column(name="EARLIER_EXP_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(timezone = GMT_PE,pattern = DateUtils.DEFAULT_DATETIME_PATTERN)
    private Date earlierExpDate;

    /**
     * 原材料发货日期
     */
    @Column(name="SHIPPING_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(timezone = GMT_PE,pattern = DateUtils.DEFAULT_DATETIME_PATTERN)
    private Date shippingDate;

    /**
     * GlaxyCore MES完成品的levelTwoCode
     */
    @Column(name="RESERVED1")
    private String reserved1;

    /**
     * GlaxyCore MES完成品的waferId
     */
    @Column(name="RESERVED2")
    private String reserved2;

    /**
     * GlaxyCore MES完成品的salesNote
     */
    @Column(name="RESERVED3")
    private String reserved3;

    /**
     * GlaxyCore MES完成品的treasuryNote
     */
    @Column(name="RESERVED4")
    private String reserved4;

    /**
     * GlaxyCore MES完成品的productionNote
     */
    @Column(name="RESERVED5")
    private String reserved5;

    /**
     * GlaxyCore MES完成品的bondedProperty
     */
    @Column(name="RESERVED6")
    private String reserved6;

    /**
     * GlaxyCore MES完成品的productCategory
     */
    @Column(name="RESERVED7")
    private String reserved7;

    /**
     * GlaxyCore 中转箱号
     */
    @Column(name="RESERVED8")
    private String reserved8;

    /**
     * GlaxyCore 装箱检验判定等级
     */
    @Column(name="RESERVED9")
    private String reserved9;

    /**
     * GlaxyCore 装箱检验判定码
     */
    @Column(name="RESERVED10")
    private String reserved10;

    /**
     * GlaxyCore 重测之后的工单号
     */
    @Column(name="RESERVED11")
    private String reserved11;

    /**
     * GlaxyCore documentLineRrn
     */
    @Column(name="RESERVED12")
    private String reserved12;

    /**
     * GlaxyCore 接收的时候会记录默认仓库
     */
    @Column(name="RESERVED13")
    private String reserved13;

    /**
     * GlaxyCore 记录StorageId
     */
    @Column(name="RESERVED14")
    private String reserved14;

    /**
     * GlaxyCore 记录重测工单日期
     */
    @Column(name="RESERVED15")
    private String reserved15;

    /**
     * GlaxyCore 记录备货的出货单LineRrn
     */
    @Column(name="RESERVED16")
    private String reserved16;

    /**
     * GlaxyCore 记录备货的出货单号
     */
    @Column(name="RESERVED17")
    private String reserved17;

    /**
     * GlaxyCore 备货备注
     */
    @Column(name="RESERVED18")
    private String reserved18;

    /**
     * GlaxyCore 记录箱重量
     */
    @Column(name="RESERVED19")
    private String reserved19;

    /**
     * GlaxyCore 多箱称重事务号
     */
    @Column(name="RESERVED20")
    private String reserved20;

    /**
     * GlaxyCore 产品编码(工单号在ETM_WO中的other7)
     */
    @Column(name="RESERVED21")
    private String reserved21;

    /**
     * GlaxyCore 来料导入packing list中Sub Name
     */
    @Column(name="RESERVED22")
    private String reserved22;

    /**
     * GlaxyCore 来料导入packing list中Ship To
     */
    @Column(name="RESERVED23")
    private String reserved23;

    /**
     * GlaxyCore 来料导入packing list中Fab Device
     */
    @Column(name="RESERVED24")
    private String reserved24;

    /**
     * GlaxyCore 来料导入packing list中Lot Type
     */
    @Column(name="RESERVED25")
    private String reserved25;

    /**
     * GlaxyCore 来料导入packing list中Wafer Property
     */
    @Column(name="RESERVED26")
    private String reserved26;

    /**
     * GlaxyCore 来料导入packing list中PO NO
     */
    @Column(name="RESERVED27")
    private String reserved27;

    /**
     * GlaxyCore 来料导入packing list中Ship Out Date
     */
    @Column(name="RESERVED28")
    private String reserved28;

    /**
     * GlaxyCore 来料导入packing list中Invoice NO
     */
    @Column(name="RESERVED29")
    private String reserved29;

    /**
     * GlaxyCore 来料导入packing list中FAB Lot ID
     */
    @Column(name="RESERVED30")
    private String reserved30;

    /**
     * GlaxyCore 来料导入packing list中Wafer ID
     */
    @Column(name="RESERVED31")
    private String reserved31;

    /**
     * GlaxyCore 来料导入packing list中Gross Dies
     */
    @Column(name="RESERVED32")
    private String reserved32;

    /**
     * GlaxyCore 来料导入packing list中Sampling Qty
     */
    @Column(name="RESERVED33")
    private String reserved33;

    /**
     * GlaxyCore 来料导入packing list中Pass Dies
     */
    @Column(name="RESERVED34")
    private String reserved34;

    /**
     * GlaxyCore 来料导入packing list中NG Die
     */
    @Column(name="RESERVED35")
    private String reserved35;

    /**
     * GlaxyCore 来料导入packing list中Yield
     */
    @Column(name="RESERVED36")
    private String reserved36;

    /**
     * GlaxyCore 来料导入packing list中Pack Lot ID
     */
    @Column(name="RESERVED37")
    private String reserved37;

    /**
     * GlaxyCore 来料导入packing list中Wafer Mark
     */
    @Column(name="RESERVED38")
    private String reserved38;

    /**
     * GlaxyCore 来料导入packing list中Carton NO
     */
    @Column(name="RESERVED39")
    private String reserved39;

    /**
     * GlaxyCore 来料导入packing list中Type
     */
    @Column(name="RESERVED40")
    private String reserved40;

    /**
     * GlaxyCore 来料导入packing list中Remark
     */
    @Column(name="RESERVED41")
    private String reserved41;

    /**
     * GlaxyCore 来料导入packing list中Pass Dies2
     */
    @Column(name="RESERVED42")
    private String reserved42;

    /**
     * GlaxyCore 来料导入packing list中Pass Dies3
     */
    @Column(name="RESERVED43")
    private String reserved43;

    /**
     * GlaxyCore 来料导入packing list中Frame Qty
     */
    @Column(name="RESERVED44")
    private String reserved44;

    /**
     * GlaxyCore 来料导入packing list中Pcode
     */
    @Column(name="RESERVED45")
    private String reserved45;

    /**
     * GlaxyCore 来料导入packing list中WO、WO NO
     */
    @Column(name="RESERVED46")
    private String reserved46;

    /**
     * GlaxyCore 来料导入文件名称
     */
    @Column(name="RESERVED47")
    private String reserved47;

    /**
     * GlaxyCore 来料导入的导入编码
     */
    @Column(name="RESERVED48")
    private String reserved48;

    /**
     * GlaxyCore 来料导入的导入类型
     */
    @Column(name="RESERVED49")
    private String reserved49;

    /**
     * GlaxyCore Wafer Source
     */
    @Column(name="RESERVED50")
    private String reserved50;

    /**
     * GlaxyCore 收货地址
     */
    @Column(name="RESERVED51")
    private String reserved51;

    /**
     * shipper_person 具体收货人
     */
    @Column(name="RESERVED52")
    private String reserved52;

    /**
     * shipperPhone 具体收货电话
     */
    @Column(name="RESERVED53")
    private String reserved53;

    /**
     * shipperPhone 晶圆出货标记出货形态
     */
    @Column(name="RESERVED54")
    private String reserved54;

    /**
     * shipperPhone 晶圆出货标记客户简称
     */
    @Column(name="RESERVED55")
    private String reserved55;

    /**
     * shipperPhone 晶圆出货标记PO
     */
    @Column(name="RESERVED56")
    private String reserved56;

    /**
     * shipperPhone 晶圆出货标记备注
     */
    @Column(name="RESERVED57")
    private String reserved57;

    /**
     * spareSpecs 备件规格
     */
    @Column(name="RESERVED58")
    private String reserved58;

    /**
     *  spareModel 备件型号
     */
    @Column(name="RESERVED59")
    private String reserved59;

    /**
     * sparePartsLine 备件线别
     */
    @Column(name="VENDER_ADDRESS")
    private String venderAddress;

    /**
     * sparePartsLine 备件线别
     */
    @Column(name="RESERVED60")
    private String reserved60;

    /**
     * PACK_DEVICE
     */
    @Column(name="PACK_DEVICE")
    private String packDevice;

    /**
     * 工程师名
     */
    @Column(name="ENGINEER_NAME")
    private String engineerName;

    /**
     * 实验目的
     */
    @Column(name="TEST_PURPOSE")
    private String testPurpose;

    /**
     * 工程备注
     */
    @Column(name="WORK_REMARKS")
    private String workRemarks;

    @Transient
    private String documentLineUser;

    /**
     * 理论重量
     */
    @Transient
    private BigDecimal theoryWeight;

    /**
     * 浮动值
     */
    @Transient
    private BigDecimal floatValue;

    /**
     * 原料生产日期 用于页面接收
     */
    @Transient
    private String mfgDateValue;
    /**
     * 原料有效日期 用于页面接收
     */
    @Transient
    private String expDateValue;
    /**
     * 原料发货日期 用于页面接收
     */
    @Transient
    private String shippingDateValue;

    @Transient
    private Integer scanSeq;

    @Transient
    private String boxsScanSeq;

    @Transient
    private String tapeMaterialCode;

    @Transient
    private String treasuryNote;

    /**
     * 验证物料批次是否在有效期内
     */
    public void validationEffective() {
        if (effectiveLife != null && !StringUtils.isNullOrEmpty(effectiveUnit)) {
            Date effectiveDate = DateUtils.plus(receiveDate, effectiveLife.intValue(), effectiveUnit);
            if (!effectiveDate.after(new Date())) {
                throw new ClientException(MmsException.MM_MATERIAL_LOT_HAS_EXPIRED);
            }
        }
    }

    public void setSubMaterialLotFlag(Boolean subMaterialLotFlag) {
        this.subMaterialLotFlag = subMaterialLotFlag ? StringUtils.YES : StringUtils.NO;
    }

    public boolean getSubMaterialLotFlag() {
        return StringUtils.YES.equalsIgnoreCase(this.subMaterialLotFlag);
    }

    @Override
    public String getSubStatus() {
        return null;
    }

    @Override
    public String getPreSubStatus() {
        return null;
    }

    @Override
    public void setSubStatus(String subState) {

    }

    @Override
    public void setPreSubStatus(String subStatus) {

    }

    @PrePersist
    protected void prePersist() {
        super.prePersist();
        if (this.created == null) {
            created = new Date();
        }
        updated = new Date();
        createdBy = ThreadLocalContext.getUsername();
        updatedBy = ThreadLocalContext.getUsername();
    }

    /**
     * 恢复前置状态
     *  将前置状态当成当前状态，当前状态变成前置状态
     */
    public void restoreStatus() {
        String currentStatusCategory = this.getStatusCategory();
        String currentStatus = this.getStatus();
        String currentSubStatus = this.getSubStatus();

        this.setStatusCategory(this.getPreStatusCategory());
        this.setStatus(this.getPreStatus());
        this.setSubStatus(this.getPreSubStatus());

        this.setPreStatusCategory(currentStatusCategory);
        this.setPreStatus(currentStatus);
        this.setPreSubStatus(currentSubStatus);
    }
    /**
     * 验证了批次是否被Hold 如果被Hold则抛出异常
     * @throws ClientException
     */
    public MaterialLot validateMLotHold() throws ClientException{
        if (HOLD_STATE_ON.equals(holdState)) {
            throw new ClientException(MmsException.MM_MATERIAL_LOT_ALREADY_HOLD);
        }
        return this;
    }

    public void isFinish() {
        if (MaterialStatusCategory.STATUS_CATEGORY_FIN.equals(this.getStatusCategory())) {
            throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_ALREADY_FIN, this.getMaterialLotId());
        }
    }

    public void initialMaterialLot() {
        setReceiveDate(new Date());
        setPreStatusCategory(StringUtils.EMPTY);
        setPreStatus(StringUtils.EMPTY);
        setPreSubStatus(StringUtils.EMPTY);
        setReceiveQty(this.getCurrentQty());
        setReservedQty(BigDecimal.ZERO);
    }

    /**
     * 清空快递相关信息
     */
    public void clearExpressInfo() {
        this.setExpressNumber(StringUtils.EMPTY);
        this.setExpressCompany(StringUtils.EMPTY);
        this.setPlanOrderType(StringUtils.EMPTY);
    }

    /**
     * 清空预留相关栏位信息
     */
    public void clearReservedInfo() {
        this.setReserved16(StringUtils.EMPTY);
        this.setReserved17(StringUtils.EMPTY);
        this.setReserved18(StringUtils.EMPTY);

        this.setDocDate(null);
        this.setShipper(StringUtils.EMPTY);
        this.setReserved51(StringUtils.EMPTY);
        this.setReserved52(StringUtils.EMPTY);
        this.setReserved53(StringUtils.EMPTY);
    }

    /**
     * 清空被包装批次的相关信息
     */
    public void clearPackedMaterialLot() {
        //TODO 此处为GC客制化
        // 清除中转箱号以及库位号
        this.setReserved8(StringUtils.EMPTY);
        this.setReserved14(StringUtils.EMPTY);
    }

    /**
     * 构建包装批次
     * @param packageType
     */
    public void buildPackageMaterialLot(String packageType) {
        this.initialMaterialLot();
        this.setStatusCategory(MaterialStatusCategory.STATUS_CATEGORY_USE);
        this.setStatus(MaterialStatus.STATUS_WAIT);
        this.setPackageType(packageType);

        //TODO 此处为GC客制化
        // 清除中转箱号以及库位号 清空场外LOTID号
        this.setReserved8(StringUtils.EMPTY);
        this.setLotId(StringUtils.EMPTY);
        if(!RW_PACKCASE.equals(packageType)){
            this.setReserved14(StringUtils.EMPTY);
        }

        // 清空备货相关信息
        //this.setReserved16(StringUtils.EMPTY);
        //this.setReserved17(StringUtils.EMPTY);
        //.setReserved18(StringUtils.EMPTY);
    }

    public void setMaterial(Material material) {
        this.setMaterialRrn(material.getObjectRrn());
        this.setMaterialName(material.getName());
        this.setMaterialDesc(material.getDescription());
        this.setMaterialVersion(material.getVersion());
        this.setMaterialCategory(material.getMaterialCategory());
        this.setMaterialType(material.getMaterialType());
        this.setStoreUom(material.getStoreUom());
    }

    public void setMaterialLot(MaterialLot materialLot) {
        this.setGrade(materialLot.getGrade());
        this.setReserved1(materialLot.getReserved1());
        this.setReserved6(materialLot.getReserved6());
        this.setReserved13(materialLot.getReserved13());
        this.setReserved14(materialLot.getReserved14());
        this.setReserved22(materialLot.getReserved22());
        this.setReserved23(materialLot.getReserved23());
        this.setReserved27(materialLot.getReserved27());
        this.setReserved28(materialLot.getReserved28());
        this.setReserved29(materialLot.getReserved29());
        this.setReserved39(materialLot.getReserved39());
        this.setReserved41(materialLot.getReserved41());
        this.setReserved46(materialLot.getReserved46());
        this.setReserved47(materialLot.getReserved47());
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
