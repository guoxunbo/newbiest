package com.newbiest.mms.model;

import com.newbiest.base.model.NBUpdatable;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 单据对应的详细信息
 * Created by guoxunbo on 2019-08-29 17:51
 */
@Data
@Table(name = "MMS_DOCUMENT_LINE")
@Entity
public class DocumentLine extends NBUpdatable {

    //发货类型
    public static final String MP_SHIPPING_TYPE = "量产";
    public static final String ES_SHIPPING_TYPE = "工程样品";
    public static final String CS_SHIPPING_TYPE = "客户样品";

    @Column(name="DOC_RRN")
    private String docRrn;

    @Column(name="DOC_ID")
    private String docId;

    @Column(name="LINE_ID")
    private String lineId;

    @Column(name="DOC_CATEGORY")
    private String docCategory;

    @Column(name="MATERIAL_RRN")
    private String materialRrn;

    @Column(name="MATERIAL_NAME")
    private String materialName;

    @Column(name="QTY")
    private BigDecimal qty;

    @Column(name="HANDLED_QTY")
    private BigDecimal handledQty = BigDecimal.ZERO;

    @Column(name="UN_HANDLE_QTY")
    private BigDecimal unHandledQty = BigDecimal.ZERO;

    @Column(name="RESERVED_QTY")
    private BigDecimal reservedQty = BigDecimal.ZERO;

    @Column(name="UN_RESERVED_QTY")
    private BigDecimal unReservedQty = BigDecimal.ZERO;

    /**
     *shipping Date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "SHIPPING_DATE")
    private Date shippingDate;

    /**
     * 状态
     */
    private String status;

    /**
     *PID
     */
    @Column(name="RESERVED1")
    private String reserved1;

    /**
     * 客户代码
     */
    @Column(name="RESERVED2")
    private String reserved2;

    /**
     *版本
     */
    @Column(name="RESERVED3")
    private String reserved3;

    /**
     *等级
     */
    @Column(name="RESERVED4")
    private String reserved4;

    /**
     *MRB
     */
    @Column(name="RESERVED5")
    private String reserved5;

    /**
     *remark
     */
    @Column(name="RESERVED6")
    private String reserved6;

    /**
     *承运人
     */
    @Column(name="RESERVED7")
    private String reserved7;

    /**
     *物流信息
     */
    @Column(name="RESERVED8")
    private String reserved8;

    /**
     *关务手册号
     */
    @Column(name="RESERVED9")
    private String reserved9;

    /**
     *是否保税
     */
    @Column(name="RESERVED10")
    private String reserved10;

    /**
     *客户名称
     */
    @Column(name="RESERVED11")
    private String reserved11;

    /**
     *Sold-to
     */
    @Column(name="RESERVED12")
    private String reserved12;

    /**
     *Sold-Add
     */
    @Column(name="RESERVED13")
    private String reserved13;

    /**
     *Contact&Tel
     */
    @Column(name="RESERVED14")
    private String reserved14;

    /**
     *Ship-to
     */
    @Column(name="RESERVED15")
    private String reserved15;

    /**
     *Ship-Add
     */
    @Column(name="RESERVED16")
    private String reserved16;

    /**
     *Contact
     */
    @Column(name="RESERVED17")
    private String reserved17;

    /**
     *Tel
     */
    @Column(name="RESERVED18")
    private String reserved18;

    /**
     *SO No
     */
    @Column(name="RESERVED19")
    private String reserved19;

    /**
     *Customer PO
     */
    @Column(name="RESERVED20")
    private String reserved20;

    /**
     *INV NO
     */
    @Column(name="RESERVED21")
    private String reserved21;

    /**
     * 客户产品
     */
    @Column(name="RESERVED22")
    private String reserved22;

    /**
     * 配料单流水码
     */
    @Column(name="RESERVED23")
    private String reserved23;

    /**
     * 对应备货规则
     */
    @Column(name="RESERVED24")
    private String reserved24;

    /**
     * Delivery Term:
     */
    @Column(name="RESERVED25")
    private String reserved25;

    /**
     * 备件编码
     */
    @Column(name="RESERVED26")
    private String reserved26;

    /**
     * 发货类型
     * 1.量产
     * 2.工程样品
     * 3.客户样品
     */
    @Column(name="RESERVED27")
    private String reserved27;

    /**
     * 仓库代码
     */
    @Column(name="RESERVED28")
    private String reserved28;

    /**
     * 采购订单号
     */
    @Column(name="RESERVED29")
    private String reserved29;

    /**
     * 行项目号
     */
    @Column(name="RESERVED30")
    private String reserved30;

    /**
     * 发运港
     */
    @Column(name="RESERVED31")
    private String reserved31;

    /**
     * RMA号
     */
    @Column(name="RESERVED32")
    private String reserved32;

    /**
     * 领料人
     */
    @Column(name="RESERVED33")
    private String reserved33;

    /**
     * 产品描述
     */
    @Column(name="RESERVED34")
    private String reserved34;

    /**
     * 位置描述
     */
    @Column(name="RESERVED35")
    private String reserved35;
    /**
     * 导入时格式转换
     */
    @Transient
    private String shippingDateValue;

    /**
     * 创建ByReelCode发料单时，接收ReelCode
     * 一个Excel表示一个发货单·
     */
    @Transient
    private String reelCodeId;

    /**
     * 创建通过客户版本发料单时，接收客户版本
     */
    @Transient
    private String customerVersion;

    /**
     * 创建通过客户产品发料单时，接收客户产品
     */
    @Transient
    private String customerProduct;

    public void setMaterial(Material material) {
        this.materialRrn = material.getObjectRrn();
        this.materialName = material.getName();
    }

    public void setDocument(Document document) {
        this.docRrn = document.getObjectRrn();
        this.docId = document.getName();
        this.docCategory = document.getCategory();
    }
}
