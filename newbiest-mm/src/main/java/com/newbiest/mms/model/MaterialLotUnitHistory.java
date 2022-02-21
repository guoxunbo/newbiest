package com.newbiest.mms.model;

import com.newbiest.base.model.NBHis;
import com.newbiest.base.model.NBUpdatable;
import com.newbiest.base.utils.ThreadLocalContext;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 物料批次的单元数据。比如接收以单元的方式进行。组装个一个materiLot。
 *
 * Created by guoxunbo on 2020-01-17 10:53
 */
@Entity
@Table(name="MMS_MATERIAL_LOT_UNIT_HIS")
@Data
public class MaterialLotUnitHistory extends NBHis {

    public static final String TRANS_TYPE_IN = "In";
    public static final String TRANS_TYPE_STOCK_OUT = "Out";
    public static final String TRANS_TYPE_THROW_WLT_OUT = "ThrowWltOut";


    public static final String PRODUCT_TYPE = "PROD";

    /**
     * 物料批次单元号
     */
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
    private String state;

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

    @Column(name="TRANS_QTY")
    private BigDecimal transQty;

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
     * GlaxyCore 导入类型
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

    public void setCreated(Date created) {
        this.created = created;
    }
}
