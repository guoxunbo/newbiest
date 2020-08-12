package com.newbiest.mms.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.newbiest.base.model.NBUpdatable;
import com.newbiest.base.model.NBVersionControl;
import com.newbiest.base.ui.model.NBReferenceList;
import com.newbiest.base.utils.DateUtils;
import lombok.Data;
import lombok.Getter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 单据
 * Created by guoxunbo on 2019-08-29 17:19
 */
@Data
@Entity
@Table(name="MMS_DOCUMENT")
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="CATEGORY", discriminatorType = DiscriminatorType.STRING, length = 32)
public class Document extends NBUpdatable {

    public static final String CATEGORY_DELIVERY = "Delivery";
    public static final String CATEGORY_RECEIVE = "Receive";

    public static final String STATUS_OPEN = "OPEN";

    @Column(name="NAME")
    private String name;

    @Column(name="DESCRIPTION")
    private String description;

    @Column(name = "OWNER")
    private String owner;

    @Column(name="STATUS")
    private String status;

    @Column(name="CATEGORY", updatable = false, insertable = false)
    private String category;

    /**
     * 单据总数量。
     *  由lines上统计。无法私自改数量
     */
    @Column(name="QTY")
    private BigDecimal qty;

    /**
     * 已经处理的数量
     *  由lines上统计。无法私自改数量
     */
    @Column(name="HANDLED_QTY")
    private BigDecimal handledQty = BigDecimal.ZERO;

    /**
     * 未处理的数量
     *  由lines上统计。无法私自改数量
     */
    @Column(name="UN_HANDLE_QTY")
    private BigDecimal unHandledQty = BigDecimal.ZERO;

    /**
     * 客户或者供应商名称
     */
    @Column(name="SUPPLIER_NAME")
    private String supplierName;

    /**
     * 责任日期
     */
    @Column(name="DUE_DATE")
    private Date dueDate;

    @Column(name="ERP_CREATED", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(timezone = GMT_PE,pattern = DateUtils.DEFAULT_DATE_PATTERN)
    @Getter
    protected Date erpCreated;

    @Column(name="RESERVED_QTY")
    private BigDecimal reservedQty = BigDecimal.ZERO;

    @Column(name="UN_RESERVED_QTY")
    private BigDecimal unReservedQty = BigDecimal.ZERO;

//    @OneToMany(fetch= FetchType.LAZY, cascade={CascadeType.ALL})
//    @JoinColumn(name = "DOC_RRN", referencedColumnName = "OBJECT_RRN")
    @Transient
    private List<DocumentLine> documentLines;

    /**
     * 关联 ERP CVOUCHTYPE  单据类型
     */
    @Column(name="DOC_TYPE")
    private String docType;

    /**
     * 关联 ERP CVOUCHNAME  单据名称
     */
    @Column(name="DOC_NAME")
    private String docName;

    /**
     * 关联 ERP CBUSTYPE  单据业务类型
     */
    @Column(name="DOC_BUS_TYPE")
    private String docBusType;

    /**
     * 关联 ERP CSOURCE  单据源
     */
    @Column(name="DOC_SOURCE")
    private String docSource;

    /**
     * 关联 ERP CWHCODE  转入/转出仓库编码
     */
    @Column(name="WAREHOUSE_CODE")
    private String warehouseCode;

    /**
     * 关联 ERP CWHNAME  仓库名称
     */
    @Column(name="WAREHOUSE_NAME")
    private String warehouseName;

    @Column(name="RESERVED1")
    private String reserved1;

    @Column(name="RESERVED2")
    private String reserved2;

    @Column(name="RESERVED3")
    private String reserved3;

    @Column(name="RESERVED4")
    private String reserved4;

    @Column(name="RESERVED5")
    private String reserved5;

    @Column(name="RESERVED6")
    private String reserved6;

    @Column(name="RESERVED7")
    private String reserved7;

    @Column(name="RESERVED8")
    private String reserved8;

    @Column(name="RESERVED9")
    private String reserved9;

    /**
     * 关联 ERP G_CODE
     */
    @Column(name="RESERVED10")
    private String reserved10;

    /**
     * 关联 ERP G_NAME
     */
    @Column(name="RESERVED11")
    private String reserved11;

    /**
     * 关联 ERP CUSABBNAME
     */
    @Column(name="RESERVED12")
    private String reserved12;

    /**
     * 关联 ERP MEMO
     */
    @Column(name="RESERVED13")
    private String reserved13;

    /**
     * 关联 ERP ITEMCODE
     */
    @Column(name="RESERVED14")
    private String reserved14;

    /**
     * 关联 ERP SHIPADDRESS
     */
    @Column(name="RESERVED15")
    private String reserved15;

    /**
     * 关联 ERP OTHER2
     */
    @Column(name="RESERVED16")
    private String reserved16;

    /**
     * 关联 ERP OTHER3
     */
    @Column(name="RESERVED17")
    private String reserved17;

    /**
     * 关联 ERP OTHER6
     */
    @Column(name="RESERVED18")
    private String reserved18;

    /**
     * 关联 ERP OTHER8
     */
    @Column(name="RESERVED19")
    private String reserved19;

    /**
     * 关联 ERP OTHER9
     */
    @Column(name="RESERVED20")
    private String reserved20;

    /**
     * 关联 ERP OTHER10
     */
    @Column(name="RESERVED21")
    private String reserved21;

    /**
     * 关联 ERP OTHER11
     */
    @Column(name="RESERVED22")
    private String reserved22;

    /**
     * 关联 ERP OTHER12
     */
    @Column(name="RESERVED23")
    private String reserved23;

    /**
     * 关联 ERP OTHER13
     */
    @Column(name="RESERVED24")
    private String reserved24;

    /**
     * 关联 ERP OTHER14
     */
    @Column(name="RESERVED25")
    private String reserved25;

    /**
     * 关联 ERP OTHER15
     */
    @Column(name="RESERVED26")
    private String reserved26;

    @Column(name="RESERVED27")
    private String reserved27;

    @Column(name="RESERVED28")
    private String reserved28;

    @Column(name="RESERVED29")
    private String reserved29;

    @Column(name="RESERVED30")
    private String reserved30;

    @Column(name="RESERVED31")
    private String reserved31;

    @Column(name="RESERVED32")
    private String reserved32;

    @Column(name="RESERVED33")
    private String reserved33;

    @Column(name="RESERVED34")
    private String reserved34;

    @Column(name="RESERVED35")
    private String reserved35;

    @Column(name="RESERVED36")
    private String reserved36;

    @Column(name="RESERVED37")
    private String reserved37;

    @Column(name="RESERVED38")
    private String reserved38;

    @Column(name="RESERVED39")
    private String reserved39;

    @Column(name="RESERVED40")
    private String reserved40;

}
