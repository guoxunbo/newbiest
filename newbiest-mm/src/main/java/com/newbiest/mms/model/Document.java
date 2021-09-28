package com.newbiest.mms.model;

import com.newbiest.base.model.NBUpdatable;
import com.newbiest.base.threadlocal.ThreadLocalContext;
import com.newbiest.base.utils.DateUtils;
import lombok.Data;

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

    public static final String CATEGORY_ISSUE_FINISH_GOOD= "IssueFinishGood";
    public static final String CATEGORY_DELIVERY = "Delivery";
    public static final String CATEGORY_INCOMING = "Incoming";
    public static final String CATEGORY_ISSUE_LOT = "IssueLot";
    public static final String CATEGORY_ISSUE_MATERIAL = "IssueMaterial";
    public static final String CATEGORY_ISSUE_PARTS = "IssueParts";
    public static final String CATEGORY_CHECK = "Check";
    public static final String CATEGORY_SCRAP = "Scrap";

    /**
     * 指定物料发料以及数量，不管主辅材或实验室
     */
    public static final String CATEGORY_ISSUE_BY_MATERIAL = "IssueByMaterial";

    /**
     * 指定物料批次发料以及数量，不管主辅材或实验室
     */
    public static final String CATEGORY_ISSUE_BY_MLOT = "IssueByMLot";

    /**
     * 产线退料到仓库
     */
    public static final String CATEGORY_RETURN = "Return";

    /**
     * 仓库退料到供应商
     */
    public static final String CATEGORY_RETURN_MLOT = "ReturnMLot";

    /**
     * 部门退料
     */
    public static final String CATEGORY_DEPT_RETURN = "DeptReturn";

    /**
     * 客户退货
     */
    public static final String CATEGORY_RETURN_LOT = "ReturnLot";

    public static final String CATEGORY_FINISH_GOOD = "FinishGood";

    /**
     * RMA来料  自身原因,由SAP导入
     */
    public static final String CATEGORY_RMA_INCOMING = "RmaIncoming";

    /**
     * RMA来料2  非自身原因,由WMS导入
     */
    public static final String CATEGORY_RMA_INCOMING2 = "RmaIncoming2";

    public static final String STATUS_CREATE = "Create";
    public static final String STATUS_APPROVE = "Approve";
    public static final String STATUS_Reject = "Reject";

    public static final String STATUS_CLOSE = "Close";
    public static final String STATUS_WAIT_RECHECK = "WaitRecheck";

    @Column(name="NAME")
    private String name;

    @Column(name="DESCRIPTION")
    private String description;

    @Column(name = "OWNER")
    private String owner;

    @Column(name="STATUS")
    private String status = STATUS_CREATE;

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

    @Column(name="APPROVE_USER")
    private String approveUser;

    @Column(name="APPROVE_TIME")
    private Date approveTime;

    @Column(name = "SHIPPING_DATE")
    private Date shippingDate;

    @Transient
    private List<DocumentLine> documentLines;

    @Transient
    private List<MaterialLot> materialLots ;

    /**
     * 领料人
     */
    @Column(name="RESERVED1")
    private String reserved1;

    /**
     * 成品中心
     */
    @Column(name="RESERVED2")
    private String reserved2;

    /**
     * RMA号码
     */
    @Column(name="RESERVED3")
    private String reserved3;

    /**
     * 物料信息
     */
    @Column(name="RESERVED4")
    private String reserved4;

    /**
     * 地址
     */
    @Column(name="RESERVED5")
    private String reserved5;

    /**
     * 电话
     */
    @Column(name="RESERVED6")
    private String reserved6;

    /**
     * 联系人
     */
    @Column(name="RESERVED7")
    private String reserved7;

    @Column(name="RESERVED8")
    private String reserved8;

    @Column(name="RESERVED9")
    private String reserved9;

    @Column(name="RESERVED10")
    private String reserved10;

    @PreUpdate
    protected void preUpdate() {
        super.preUpdate();
        if (STATUS_APPROVE.equals(status)) {
            approveTime = DateUtils.now();
            approveUser = ThreadLocalContext.getUsername();
        }
    }

}
