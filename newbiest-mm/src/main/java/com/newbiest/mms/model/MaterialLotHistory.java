package com.newbiest.mms.model;

import com.newbiest.base.dto.Action;
import com.newbiest.base.model.NBHis;
import com.newbiest.base.utils.StringUtils;
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

    public static final String TRANS_TYPE_RECEIVE = "Receive";
    public static final String TRANS_TYPE_STOCK_IN = "StockIn";
    public static final String TRANS_TYPE_STOCK_OUT = "StockOut";
    public static final String TRANS_TYPE_SHIP = "Ship";
    public static final String TRANS_TYPE_ISSUE = "Issue";
    public static final String TRANS_TYPE_RETURN = "Return";
    public static final String TRANS_TYPE_CREATE_RETURN_ORDER = "CreateReturnOrder";
    public static final String TRANS_TYPE_CREATE_ISSE_LOT_ORDER = "CreateIssueLotOrder";
    public static final String TRANS_TYPE_CREATE_ISSUE_MATERIAL_ORDER = "CreateIssueMaterialOrder";
    public static final String TRANS_TYPE_CREATE_ISSUE_ORDER = "CreateIssueOrder";

    public static final String TRANS_TYPE_PICK = "Pick";
    public static final String TRANS_TYPE_TRANSFER = "Transfer";
    public static final String TRANS_TYPE_CHECK = "Check";
    public static final String TRANS_TYPE_CONSUME = "Consume";

    public static final String TRANS_TYPE_PACKAGE = "Package";
    public static final String TRANS_TYPE_UN_PACKAGE = "UnPackage";

    public static final String TRANS_TYPE_SPLIT = "Split";
    public static final String TRANS_TYPE_SPLIT_CREATE = "SplitCreate";

    /**
     * 在ByReel发货单导入时
     * 物料绑定单据
     */
    public static final String TRANS_TYPE_PRE_RESERVED = "PreReserved";
    public static final String TRANS_TYPE_RESERVED = "Reserved";
    public static final String TRANS_TYPE_UNRESERVED = "unReserved";
    public static final String TRANS_TYPE_WEIGHT = "Weight";
    public static final String TRANS_TYPE_PACK_CKECK = "PackCkeck";
    /**
     * 因为包装产生的批次
     */
    public static final String TRANS_TYPE_CREATE_PACKAGE = "CreatePackage";
    public static final String TRANS_TYPE_ADDITIONAL_PACKAGE = "AdditionalPackage";

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
    private String statusModelRrn;

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
    private String parentMaterialLotRrn;

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
     * 物料主键
     */
    @Column(name="MATERIAL_RRN")
    private String materialRrn;

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
    private Date receiveDate;

    /**
     * 有效时长
     */
    @Column(name="EFFECTIVE_LIFE")
    private Double effectiveLife;

    /**
     * 警告时长
     * 当达到此时长的时候触发警告
     */
    @Column(name="WARNING_LIFE")
    private Double warningLife;

    /**
     * 有效时长单位
     */
    @Column(name="EFFECTIVE_UNIT")
    private String effectiveUnit;

    /**
     * 到期时间
     */
    @Column(name="EXPIRE_DATE")
    private Date expireDate;

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
     * 最近入库的库位号 只具备显示意义，不具备统计意义
     */
    @Column(name="LAST_STORAGE_ID")
    private String lastStorageId;

    /**
     * 来料单据主键
     */
    @Column(name="INCOMING_DOC_RRN")
    private String incomingDocRrn;

    /**
     * 来料单据Id
     */
    @Column(name="INCOMING_DOC_ID")
    private String incomingDocId;

    /**
     * 来料备注
     */
    @Column(name="INCOMING_COMMENT")
    private String incomingComment;

    /**
     * 来料物料信息
     */
    @Column(name="INCOMING_LOG_INFO")
    private String incomingLogInfo;

    /**
     * 出货单据主键
     */
    @Column(name="SHIP_DOC_RRN")
    private String shipDocRrn;

    /**
     * 出货单据Id
     */
    @Column(name="SHIP_DOC_ID")
    private String shipDocId;

    /**
     * 出货备注
     */
    @Column(name="SHIP_COMMENT")
    private String shipComment;

    /**
     * 出货物流信息
     */
    @Column(name="SHIP_LOG_INFO")
    private String shipLogInfo;

    /**
     * 印字信息
     */
    @Column(name="LETTERING_INFO")
    private String letteringInfo;

    /**
     * 生产日期
     */
    @Column(name="PRODUCTION_DATE")
    private Date productionDate;

    /**
     * 对应的母箱的批号
     */
    @Column(name="BOX_MATERIAL_LOT_ID")
    private String boxMaterialLotId;

    /**
     *  对应的母箱的主键
     */
    @Column(name="BOX_MATERIAL_LOT_RRN")
    private String boxMaterialLotIdRrn;

    /**
     *  来料导入数量
     */
    @Column(name= "INCOMING_QTY")
    private BigDecimal incomingQty;

    /**
     *  预计达到时间
     */
    @Column(name= "EXPECTED_DELIVERY_DATE")
    private Date expectedDeliveryDate;

    /**
     * PackageSize
     */
    @Column(name="RESERVED1")
    private String reserved1;

    /**
     * PartNumber
     * 不指产品号。只做显示作用
     */
    @Column(name="RESERVED2")
    private String reserved2;

    /**
     * ParrVersion
     * 不指产品版本，只做显示作用
     */
    @Column(name="RESERVED3")
    private String reserved3;

    /**
     * ControlLot
     */
    @Column(name="RESERVED4")
    private String reserved4;

    /**
     * WaferId
     */
    @Column(name="RESERVED5")
    private String reserved5;

    /**
     * 客户订单编码
     */
    @Column(name="RESERVED6")
    private String reserved6;

    /**
     * 内部订单编码
     */
    @Column(name="RESERVED7")
    private String reserved7;

    /**
     * 委外订单号码
     */
    @Column(name="RESERVED8")
    private String reserved8;

    /**
     * D/C
     */
    @Column(name="RESERVED9")
    private String reserved9;

    /**
     * Carton Size
     */
    @Column(name="RESERVED10")
    private String reserved10;

    /**
     * Carton QTY
     */
    @Column(name="RESERVED11")
    private String reserved11;

    /**
     * N.W. (kgs)
     */
    @Column(name="RESERVED12")
    private String reserved12;

    /**
     * G.W. (kgs)
     */
    @Column(name="RESERVED13")
    private String reserved13;

    /**
     * 终端客户
     */
    @Column(name="RESERVED14")
    private String reserved14;

    /**
     * wire Type
     */
    @Column(name="RESERVED15")
    private String reserved15;

    /**
     * MRB
     */
    @Column(name="RESERVED16")
    private String reserved16;

    /**
     * 发货单位
     */
    @Column(name="RESERVED17")
    private String reserved17;

    /**
     * 是否保税
     */
    @Column(name="RESERVED18")
    private String reserved18;

    /**
     * 保税手册
     */
    @Column(name="RESERVED19")
    private String reserved19;

    /**
     * PO Number
     */
    @Column(name="RESERVED20")
    private String reserved20;

    /**
     * 中文名称
     */
    @Column(name="RESERVED21")
    private String reserved21;

    /**
     *  取件人
     */
    @Column(name="RESERVED22")
    private String reserved22;

    /**
     * 快递
     */
    @Column(name="RESERVED23")
    private String reserved23;

    /**
     * 运单号码
     */
    @Column(name="RESERVED24")
    private String reserved24;

    /**
     * 预计送达时间
     */
    @Column(name="RESERVED25")
    private String reserved25;

    /**
     * 件数
     */
    @Column(name="RESERVED26")
    private String reserved26;

    /**
     * 商品编码（HS）
     */
    @Column(name="RESERVED27")
    private String reserved27;

    /**
     * ECCN
     */
    @Column(name="RESERVED28")
    private String reserved28;

    /**
     * 关税率%
     */
    @Column(name="RESERVED29")
    private String reserved29;

    /**
     * 关税RMB
     */
    @Column(name="RESERVED30")
    private String reserved30;

    /**
     * 增值税RMB
     */
    @Column(name="RESERVED31")
    private String reserved31;

    /**
     * 进口关单号码
     */
    @Column(name="RESERVED32")
    private String reserved32;

    /**
     * 物流运费RMB
     */
    @Column(name="RESERVED33")
    private String reserved33;

    /**
     * 保险费RMB
     */
    @Column(name="RESERVED34")
    private String reserved34;

    /**
     * 其他费用RMB
     */
    @Column(name="RESERVED35")
    private String reserved35;

    /**
     * 费用合计RMB
     */
    @Column(name="RESERVED36")
    private String reserved36;

    /**
     * 贸易国别
     */
    @Column(name="RESERVED37")
    private String reserved37;

    /**
     * 发票号码
     */
    @Column(name="RESERVED38")
    private String reserved38;

    /**
     * 箱单号码
     */
    @Column(name="RESERVED39")
    private String reserved39;

    /**
     * 费用类别
     */
    @Column(name="RESERVED40")
    private String reserved40;

    /**
     * 成本中心
     */
    @Column(name="RESERVED41")
    private String reserved41;

    /**
     * 物资类别
     */
    @Column(name="RESERVED42")
    private String reserved42;

    /**
     * 描述\型号
     */
    @Column(name="RESERVED43")
    private String reserved43;

    /**
     * 记录备货的出货单LineRrn
     */
    @Column(name="RESERVED44")
    private String reserved44;

    /**
     * 记录备货的出货单Line_id 子单号
     */
    @Column(name="RESERVED45")
    private String reserved45;

    /**
     * 预留字段46
     */
    @Column(name="RESERVED46")
    private String reserved46;

    /**
     * PID
     */
    @Column(name="RESERVED47")
    private String reserved47;

    /**
     * 供应商
     */
    @Column(name="RESERVED48")
    private String reserved48;

    /**
     * 采购数量
     */
    @Column(name="RESERVED49")
    private String reserved49;

    /**
     * 采购单位
     */
    @Column(name="RESERVED50")
    private String reserved50;

    public void setSubMaterialLotFlag(Boolean subMaterialLotFlag) {
        this.subMaterialLotFlag = subMaterialLotFlag ? StringUtils.YES : StringUtils.NO;
    }

    public boolean getSubMaterialLotFlag() {
        return StringUtils.YES.equalsIgnoreCase(this.subMaterialLotFlag);
    }

    @Override
    public void setAction(Action action) {
        MaterialLotAction materialLotAction = (MaterialLotAction) action;
        super.setAction(materialLotAction);
        this.setTransQty(materialLotAction.getTransQty());

        this.setTransWarehouseId(materialLotAction.getFromWarehouseId());
        this.setTransStorageId(materialLotAction.getFromStorageId());
        this.setTargetWarehouseId(materialLotAction.getTargetWarehouseId());
        this.setTargetStorageId(materialLotAction.getTargetStorageId());

    }


}
