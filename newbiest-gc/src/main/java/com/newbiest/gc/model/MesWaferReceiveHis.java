package com.newbiest.gc.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.newbiest.base.model.NBUpdatable;
import com.newbiest.base.utils.DateUtils;
import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Glaxycore 的Mes晶圆历史表别名
 */
@Data
@Entity
@Table(name="MES_BACKEND_WAFER_RECEIVE_HIS")
public class MesWaferReceiveHis implements Serializable {

    public static final String TRNAS_TYPE_ISSUE = "ISSUE";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="OBJECT_RRN")
    private Long objectRrn;

    @Column(name="FACILITY_RRN")
    private Long facilityRrn;

    @Column(name="WAFER_ID")
    private String waferId;

    @Column(name="WAFER_NUM")
    private String waferNum;

    @Column(name="CST_ID")
    private String cstId;

    @Column(name="CST_WAFERQTY")
    private String cstWaferqty;

    @Column(name="DEVICE")
    private String device;

    @Column(name="VERSION")
    private String version;

    @Column(name="WAFER_TYPE")
    private String waferType;

    @Column(name="BOND_PRO")
    private String bondPro;

    @Column(name="SHIP_TO")
    private String shipTo;

    @Column(name="REMARK")
    private String remark;

    @Column(name="STATUS")
    private String status;

    @Column(name="IQC_REMARKS")
    private String iqcRemarks;

    @Column(name="TRANS_TYPE")
    private String transType;

    @Column(name="OPERATION_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(timezone = NBUpdatable.GMT_PE, pattern = DateUtils.DEFAULT_DATETIME_PATTERN)
    private Date operationTime = new Date();

    @Column(name="CHECK_VALUE")
    private String checkValue;

    @Column(name="CHECK_RESULT")
    private String checkResult;

    @Column(name="CHECK_REMARK")
    private String checkRemark;

    @Column(name="OPERATION_USER")
    private String operationUser;

    @Column(name="VENDER")
    private String vender;

    @Column(name="BOX_ID")
    private String boxId;

    @Column(name="PCODE")
    private String pcode;

    @Column(name="SHIP_OUT_DATE")
    private String shipOutDate ;

    @Column(name="PO_NO")
    private String poNo;

    @Column(name="CARTON_NO")
    private String cartonNo;

    @Column(name="WO")
    private String wo;

    @Column(name="INVOICE_NO")
    private String  invoiceNo;

    @Column(name="STOCK_ID")
    private String stockId;

    @Column(name="POINT_ID")
    private String pointId;

    @Column(name="PACKAGE_TYPE")
    private String packageType;

    @Column(name="WAFER_SOURCE")
    private String waferSource;

    @Column(name="IMPORT_NO")
    private String importNo;

    @Column(name="PROD_STATUS")
    private String prodStatus;

    @Column(name="DOC_NAME")
    private String docName;

    @Column(name="RELEASE_REASON")
    private String releaseReason;

    @Column(name="RELEASE_REMARK")
    private String releaseRemark;

    @Column(name="MATERIAL_RETURN_REASON")
    private String materialReturnReason;

    @Column(name="MATERIAL_RETURN_REMARK")
    private String materialReturnRemark;

    @Column(name="DELETE_REMARK")
    private String deleteRemark;

    @Column(name="WORKORDER_ID")
    private String workorderId;

    @Column(name="STORAGE_LOCATION")
    private String storageLocation;

    @Column(name="ERP_ORDER_SEQ")
    private String erpOrderSeq;

}
