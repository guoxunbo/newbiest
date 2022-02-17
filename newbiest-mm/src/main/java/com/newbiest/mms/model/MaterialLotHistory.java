package com.newbiest.mms.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.newbiest.base.model.NBHis;
import com.newbiest.base.utils.DateUtils;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.base.utils.ThreadLocalContext;
import com.newbiest.mms.dto.MaterialLotAction;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by guoxunbo on 2019/2/26.
 */
@Entity
@Table(name="MMS_MATERIAL_LOT_HIS")
@Data
public class MaterialLotHistory extends NBHis {

    public static final String TRANS_TYPE_WAFER_SOURCE_UPDATE = "WaferSourceUpdate";
    public static final String TRANS_TYPE_WAFER_UNPACK = "WaferUnpack";
    public static final String TRANS_TYPE_RECEIVE = "Receive";
    public static final String TRANS_TYPE_STOCK_IN = "StockIn";
    public static final String TRANS_TYPE_STOCK_OUT = "StockOut";
    public static final String TRANS_TYPE_SHIP = "Ship";
    public static final String TRANS_TYPE_RESERVED = "Reserved";
    public static final String TRANS_TYPE_UN_RESERVED = "UnReserved";
    public static final String TRANS_TYPE_STOCK_OUT_TAG = "StockOutTag";
    public static final String TRANS_TYPE_UN_STOCK_OUT_TAG = "UnStockOutTag";
    public static final String TRANS_TYPE_THREE_SIDE = "ThreeSide";
    public static final String TRANS_TYPE_RAW_SCRAP = "Scrap";
    public static final String TRANS_TYPE_RAW_UN_SPARE = "UnSpare";
    public static final String TRANS_TYPE_UPDATE = "Update";
    public static final String TRANS_TYPE_ADD_SHIP_ORDER_ID = "AddShipOrderId";
    public static final String TRANS_TYPE_MATERIAL_SPARE = "MaterialSpare";
    public static final String TRANS_TYPE_CANCEL_MATERIAL_SPARE = "MaterialCancel";
    public static final String TRANS_TYPE_TRANSFER_WAREHOUSE = "TransferWarehouse";
    public static final String TRANS_TYPE_SCRAP_SHIP = "ScrapShip";
    public static final String TRANS_TYPE_RAW_MATERIAL_ISSUE = "RawMaterialIssue";

    public static final String TRANS_TYPE_PICK = "Pick";
    public static final String TRANS_TYPE_TRANSFER = "Transfer";
    public static final String TRANS_TYPE_TRANSFER_PARENT = "TransferParent";
    public static final String TRANS_TYPE_CHECK = "Check";
    public static final String TRANS_TYPE_CONSUME = "Consume";

    public static final String TRANS_TYPE_PACKAGE = "Package";
    public static final String TRANS_TYPE_UN_PACKAGE = "UnPackage";

    public static final String TRANS_TYPE_RECORD_EXPRESS = "RecordExpress";
    public static final String TRANS_TYPE_CANCEL_EXPRESS = "CancelExpress";

    public static final String TRANS_TYPE_WEIGHT = "Weight";

    public static final String TRANS_TYPE_CANCEL_CHECK = "CancelCheck";

    /**
     * 因为包装产生的批次
     */
    public static final String TRANS_TYPE_CREATE_PACKAGE = "CreatePackage";
    public static final String TRANS_TYPE_ADDITIONAL_PACKAGE = "AdditionalPackage";

    /**
     * 库存有货,
     * 如果已经入库，再次入库。
     */
    public static final String TRANS_TYPE_INSTROAGE = "Instroage";

    /**
     * 产品分类
     */
    public static final String PRODUCT_TYPE = "PROD";

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
    private String holdState;

    /**
     * 批次接收数量
     * 只做记录使用不做任何运算
     */
    @Column(name="RECEIVE_QTY")
    private BigDecimal receiveQty = BigDecimal.ZERO;

    /**
     * 主数量
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
     * 物料批次类别，用来区分是普通物料批次还是包装物料批次
     */
    @Column(name="CATEGORY")
    private String category;

    /**
     * 包装类型 用于追加包装的时候进行验证
     */
    @Column(name="PACKAGE_TYPE")
    private String packageType;


    /**
     * 工单号。
     * 有些情况下工单会直接指定消耗的物料批次
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

    /**
     * 载具
     */
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
     * 操作数量
     */
    @Column(name="TRANS_QTY")
    private BigDecimal transQty;

    /**
     * 操作仓库
     */
    @Column(name="TRANS_WAREHOUSE_ID")
    private String transWarehouseId;

    /**
     * 操作库位类型
     */
    @Column(name="TRANS_STORAGE_TYPE")
    private String transStorageType;

    /**
     * 操作库位号
     */
    @Column(name="TRANS_STORAGE_ID")
    private String transStorageId;

    /**
     * 目标仓库
     */
    @Column(name="TARGET_WAREHOUSE_ID")
    private String targetWarehouseId;

    /**
     * 目标库位类型
     */
    @Column(name="TARGET_STORAGE_TYPE")
    private String targetStorageType;

    /**
     * 目标库位号
     */
    @Column(name="TARGET_STORAGE_ID")
    private String targetStorageId;

    /**
     * 载具号aliasId
     */
    @Column(name="LOT_ID")
    private String lotId;

    /**
     * 产品分类 PROD/ENG
     */
    @Column(name="PRODUCT_TYPE")
    private String productType = PRODUCT_TYPE;

    /**
     * ERP备货的单据日期
     */
    @Column(name="DOC_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(timezone = GMT_PE,pattern = DateUtils.DEFAULT_DATETIME_PATTERN)
    private Date docDate;

    /**
     * 原产品号
     */
    @Column(name="SOURCE_PRODUCT_ID")
    private String sourceProductId;

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
    @JsonFormat(timezone = GMT_PE,pattern = DateUtils.DEFAULT_DATE_PATTERN)
    private Date mfgDate;

    /**
     * 原材料有效日期
     */
    @Column(name="EXP_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(timezone = GMT_PE,pattern = DateUtils.DEFAULT_DATE_PATTERN)
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
    @JsonFormat(timezone = GMT_PE,pattern = DateUtils.DEFAULT_DATE_PATTERN)
    private Date shippingDate;

    /**
     * 记录被产品打印型号替换的产品号
     */
    @Column(name="SOURCE_MODEL_ID")
    private String sourceModelId;

    @Column(name="VENDER_ADDRESS")
    private String venderAddress;

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
     * GlaxyCore 单据号
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
     * GlaxyCore 来料导入packing list中Frame Qty、Total Qty、Wafer Qty
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
     * GlaxyCore 来料导入的文件名称
     */
    @Column(name="RESERVED47")
    private String reserved47;

    /**
     * GlaxyCore 来料导入的导入编码
     */
    @Column(name="RESERVED48")
    private String reserved48;

    /**
     * GlaxyCore 导入型号
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

    @Column(name="RESERVED58")
    private String reserved58;

    @Column(name="RESERVED59")
    private String reserved59;

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

    public void setSubMaterialLotFlag(Boolean subMaterialLotFlag) {
        this.subMaterialLotFlag = subMaterialLotFlag ? StringUtils.YES : StringUtils.NO;
    }

    public boolean getSubMaterialLotFlag() {
        return StringUtils.YES.equalsIgnoreCase(this.subMaterialLotFlag);
    }

    public void buildByMaterialLotAction(MaterialLotAction materialLotAction) {
        this.setTransQty(materialLotAction.getTransQty());
        this.setActionCode(materialLotAction.getActionCode());
        this.setActionReason(materialLotAction.getActionReason());
        this.setActionComment(materialLotAction.getActionComment());
    }

    public void setCreated(Date created) {
        this.created = created;
    }

}
