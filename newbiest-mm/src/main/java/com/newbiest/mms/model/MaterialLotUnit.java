package com.newbiest.mms.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.newbiest.base.model.NBUpdatable;
import com.newbiest.base.utils.DateUtils;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.base.utils.ThreadLocalContext;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 物料批次的具体单元数据。由一系列的单元组成一个物料批次。可以针对单元做操作。
 *  比如接收以单元的方式进行。组装个一个materiLot。
 *
 * Created by guoxunbo on 2020-01-17 10:53
 */
@Entity
@Table(name="MMS_MATERIAL_LOT_UNIT")
@Data
public class MaterialLotUnit extends NBUpdatable {

    //三星等级
    public static final String SAMSUING_GRADE = "A";

    public static final String STATE_CREATE = "Create";
    public static final String STATE_IN = "In";
    public static final String STATE_ISSUE = "Issue";
    public static final String STATE_SCRAP = "Scrap";
    public static final String STATE_OUT = "Out";
    public static final String STATE_PACKAGE = "Package";
    public static final String STATUS_MERGED = "MERGED";
    public static final String STATUS_ENGHOLD = "ENGHOLD";

    public static final String PRODUCT_TYPE_PROD = "PROD";
    public static final String PRODUCT_TYPE_ENG = "ENG";

    public static final String FAB_SENSOR = "GCFabSensor1Unmeasured";//FAB sensor(-1未测)
    public static final String FAB_SENSOR_2UNMEASURED = "GCFabSensor2Unmeasured";//FAB sensor(-2未测)
    public static final String SENSOR_CP_KLT = "GCSensorCPMeasuredKLT";//sensor CP已测（KLT）
    public static final String SENSOR_CP = "GCSensorCPMeasuredHuaLing";//sensor CP已测（-2.1华领）
    public static final String SENSOR_UNMEASURED = "GCSensorUnmeasured";//sensor未测(-2未测)
    public static final String SAMSUING_PACKING_LIST = "GCSamsungPackingList";//三星packing list(-2CP未测)
    public static final String FAB_LCD_PTC = "GCFabLCD1UnmeasuredPTC";//FAB LCD(-1未测PTC)
    public static final String FAB_LCD_SILTERRA = "GCFabLCD1UnmeasuredSilterra";//FAB LCD(-1未测Silterra)
    public static final String LCD_CP_25UNMEASURED = "GCLCDCPUnmeasured25";//LCD CP未测（-2.5未测）
    public static final String LCD_CP =  "GCLCDCPMeasured26";//LCD CP已测（-2.6已测）
    public static final String WLA_UNMEASURED = "GCWLAUnmeasured";//WLA未测（-2.5）
    public static final String WLT_PACK_RETURN =  "GCWLTPackageReturn";//WLT封装回货（-3）
    public static final String SENSOR_PACK_RETURN_COGO = "GCSensorPackageReturnCogo";//sensor封装回货（积高-3未测）
    public static final String SENSOR_PACK_RETURN = "GCSensorPackageReturn";//sensor封装回货（-3未测）
    public static final String SENSOR_TPLCC = "GCSensorTplccSenBang";//sensor-tplcc（森邦-3.5）
    public static final String SENSOR_RMA_GOOD_PRODUCT = "GCRMAGoodProductImport";//SenSor RMA良品_-3.5导入
    public static final String WLT_RMA_GOOD_PRODUCT = "GCWltRMAGoodProductImport";//WLT RMA良品_-3.5导入
    public static final String RMA_RETURN = "GCRMACustomerReturnFinishProduct";//RMA_客户退货_成品
    public static final String RMA_PURE = "GCRMAPureFinishProduct";//RMA纯_成品-4
    public static final String COB_FINISH_PRODUCT = "GCCOBFinishProduct"; //COB（-4成品）
    public static final String COB_RAW_MATERIAL_PRODUCT = "GCCOBRawMaterialProduct"; //COM原料导入
    public static final String LCD_COG_FINISH_PRODUCT = "GCLCDCOGFinishProductEcretive";//LCD（COG成品-ECRETIVE）
    public static final String LCD_COG_DETIAL = "GCLcdCogDetial";//LCD(COG成品-明细)
    public static final String FINISH_PRODUCT_IMPORT = "GCFinishProductImport";//成品导入模板
    public static final String SOC_FINISH_PRODUCT = "GCSOCFinishProduct"; //SOC成品
    public static final String SOC_WAFER_UNMEASURED = "GCSOCWaferUnmeasured"; //SOC晶圆未测、已测
    public static final String MASK_FINISH_PRODUCT= "GCMaskFinishProduct"; //MASK成品
    public static final String HN_WAREHOUSE_IMPORT = "HNWarehouseImport";//成品导入（湖南仓库）

    //产品型号
    public static final String PRODUCT_CLASSIFY_CP = "CP0";
    public static final String PRODUCT_CLASSIFY_WLA = "WLA0";
    public static final String PRODUCT_CLASSIFY_WLT = "WLT0";
    public static final String PRODUCT_CLASSIFY_SENSOR = "SENSOR0";
    public static final String PRODUCT_CLASSIFY_RMA = "RMA0";
    public static final String PRODUCT_CLASSIFY_COB = "COB0";
    public static final String PRODUCT_CLASSIFY_COG = "COG0";
    public static final String PRODUCT_CLASSIFY_SOC = "SOC0";
    public static final String PRODUCT_CLASSIFY_MASK = "MASK0";
    public static final String PRODUCT_CATEGORY_FT0 = "FT0";

    public static final String PRODUCT_CATEGORY_WLT = "WLT";
    public static final String PRODUCT_CATEGORY_CP = "CP";
    public static final String PRODUCT_CATEGORY_LCP = "LCP";
    public static final String PRODUCT_CATEGORY_SCP = "SCP";
    public static final String PRODUCT_CATEGORY_FT = "FT";
    public static final String PRODUCT_CATEGORY_WLFT = "WLFT";
    public static final String PRODUCT_CATEGORY_FT_COB = "COB";
    public static final String PRODUCT_CATEGORY_RW = "RW";
    public static final String PRODUCT_CATEGORY_SOC = "SOC";
    public static final String PRODUCT_CATEGORY_RMA = "RMA";


    public static final String BOX_TYPE = "COB";

    public static final Integer THIRTEEN = 13;

    public static final String STRING_NULL = "NULL";

    @Column(name="UNIT_ID")
    private String unitId;

    /**
     * 主物料批次主键
     */
    @Column(name="MATERIAL_LOT_RRN")
    private Long materialLotRrn;

    /**
     * 主物料批次号
     */
    @Column(name="MATERIAL_LOT_ID")
    private String materialLotId;

    /**
     * 状态
     */
    @Column(name="STATE")
    private String state = STATE_CREATE;

    /**
     * 导入时候的数量
     */
    @Column(name="RECEIVE_QTY")
    private BigDecimal receiveQty;

    /**
     * 当前数量
     */
    @Column(name="CURRENT_QTY")
    private BigDecimal currentQty;

    /**
     * die数量
     */
    @Column(name="CURRENT_SUB_QTY")
    private BigDecimal currentSubQty;

    /**
     * 等级
     */
    @Column(name="GRADE")
    private String grade;

    /**
     * 指定工单号
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
     * 载具
     */
    @Column(name="DURABLE")
    private String durable;

    /**
     * 载具上的位置
     */
    @Column(name="SLOT_NUMBER")
    private Long slotNumber;

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
     * 接收日期
     */
    @Column(name="RECEIVE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(timezone = GMT_PE,pattern = DateUtils.DEFAULT_DATETIME_PATTERN)
    private Date receiveDate;

    /**
     * 载具号aliasId
     */
    @Column(name="LOT_ID")
    private String lotId;

    /**
     * 产品分类 PROD/ENG
     */
    @Column(name="PRODUCT_TYPE")
    private String productType = PRODUCT_TYPE_PROD;

    /**
     * 入库备注
     */
    @Column(name = "TREASURY_NOTE")
    private String treasuryNote;

    /**
     * 原产品号
     */
    @Column(name="SOURCE_PRODUCT_ID")
    private String sourceProductId;

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
     * 二级代码
     */
    @Column(name="RESERVED1")
    private String reserved1;

    /**
     * 载具晶圆数量
     */
    @Column(name="RESERVED2")
    private String reserved2;

    /**
     * 硅厚
     */
    @Column(name="RESERVED3")
    private String reserved3;

    /**
     * 保税属性
     */
    @Column(name="RESERVED4")
    private String reserved4;

    /**
     * 供应商出货时间
     */
    @Column(name="RESERVED5")
    private String reserved5;

    /**
     * 委外订单
     */
    @Column(name="RESERVED6")
    private String reserved6;

    /**
     * 包装箱号
     */
    @Column(name="RESERVED7")
    private String reserved7;

    /**
     * 中转箱号
     */
    @Column(name="RESERVED8")
    private String reserved8;

    @Column(name="RESERVED9")
    private String reserved9;

    @Column(name="RESERVED10")
    private String reserved10;

    @Column(name="RESERVED11")
    private String reserved11;

    @Column(name="RESERVED12")
    private String reserved12;

    /**
     * GlaxyCore 导入的时候会记录默认仓库
     */
    @Column(name="RESERVED13")
    private String reserved13;

    @Column(name="RESERVED14")
    private String reserved14;

    @Column(name="RESERVED15")
    private String reserved15;

    @Column(name="RESERVED16")
    private String reserved16;

    @Column(name="RESERVED17")
    private String reserved17;

    /**
     * GlaxyCore mes建批lot flag，(0：未建批；1:已建批)
     */
    @Column(name="RESERVED18")
    private String reserved18;

    @Column(name="RESERVED19")
    private String reserved19;

    @Column(name="RESERVED20")
    private String reserved20;

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
     * GlaxyCore 来料导入packing list中WO
     */
    @Column(name="RESERVED46")
    private String reserved46;

    /**
     * GlaxyCore 导入文件名称
     */
    @Column(name="RESERVED47")
    private String reserved47;

    /**
     * GlaxyCore 导入编码
     */
    @Column(name="RESERVED48")
    private String reserved48;

    /**
     * GlaxyCore 导入类型 Import_TYPE
     */
    @Column(name="RESERVED49")
    private String reserved49;

    /**
     * GlaxyCore Wafer Source
     */
    @Column(name="RESERVED50")
    private String reserved50;

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
    private String relaxBoxId;

    @Transient
    private String storageId;

    /**二级代码第五位*/
    @Transient
    private String subCode5;

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
        this.setMaterialName(materialLot.getMaterialName());
        this.setReserved1(materialLot.getReserved1());
        this.setReserved22(materialLot.getReserved22());
        this.setReserved23(materialLot.getReserved23());
        this.setReserved24(materialLot.getReserved24());
        this.setReserved27(materialLot.getReserved27());
        this.setReserved28(materialLot.getReserved28());
        this.setReserved29(materialLot.getReserved29());
        this.setReserved32(materialLot.getReserved32());
        this.setReserved33(materialLot.getReserved33());
        this.setReserved34(materialLot.getReserved34());
        this.setReserved36(materialLot.getReserved36());
        this.setReserved41(materialLot.getReserved41());
        this.setReserved46(materialLot.getReserved46());
    }

    public void setRmaMaterialLot(MaterialLot materialLot) {
        this.setUnitId(materialLot.getMaterialLotId());
        this.setMaterialLotRrn(materialLot.getObjectRrn());
        this.setGrade(materialLot.getGrade());
        this.setReceiveQty(materialLot.getReceiveQty());
        this.setCurrentSubQty(BigDecimal.ONE);
        this.setCurrentQty(materialLot.getCurrentQty());
        this.setWorkOrderId(materialLot.getWorkOrderId());
        this.setWorkOrderPlanputTime(materialLot.getWorkOrderPlanputTime());
        this.setReserved4(materialLot.getReserved6());
        this.setReserved13(materialLot.getReserved13());
        this.setReserved14(materialLot.getReserved14());
        this.setReserved18("0");
        this.setDurable(materialLot.getDurable());
        this.setReserved25(materialLot.getReserved25());
        this.setReserved26(materialLot.getReserved26());
        this.setReserved30(materialLot.getReserved30());
        this.setReserved31(materialLot.getReserved31());
        this.setReserved35(materialLot.getReserved35());
        this.setReserved37(materialLot.getReserved37());
        this.setReserved38(materialLot.getReserved38());
        this.setReserved39(materialLot.getReserved39());
        this.setReserved40(materialLot.getReserved40());
        this.setReserved42(materialLot.getReserved42());
        this.setReserved43(materialLot.getReserved43());
        this.setReserved44(materialLot.getReserved44());
        this.setReserved45(materialLot.getReserved45());
        this.setReserved47(materialLot.getReserved47());
        this.setReserved48(materialLot.getReserved48());
        this.setReserved49(materialLot.getReserved49());
        this.setReserved50(materialLot.getReserved50());
        this.setProductType(materialLot.getProductType());
        this.setTreasuryNote(materialLot.getReserved4());
        this.setSourceProductId(materialLot.getSourceProductId());
    }

    public void setCreated(Date created) {
        this.created = created;
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

}
