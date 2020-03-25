package com.newbiest.gc.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.newbiest.base.model.NBUpdatable;
import com.newbiest.base.utils.DateUtils;
import com.newbiest.base.utils.ThreadLocalContext;
import com.newbiest.mms.model.MaterialLotUnit;
import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Glaxycore 的Mes晶圆表别名
 */
@Data
@Entity
@Table(name="MES_BACKEND_WAFER_RECEIVE")
public class MesWaferReceive implements Serializable {

    public static final String PACKED_STATUS_RECEIVED = "RECEIVED";

    public static final String WAFER_STATUS_LSWISSUE = "LSWISSUE";

    public static final String PACKAGR_TYPE = "K";
    public static final String POINT_ID = "EMPTY";
    public static final String PROD_STATUS = "8";
    public static final String WAFER_SOURCE = "31";

    public static final long FACILITY_RRN = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="OBJECT_RRN")
    private Long objectRrn;

    @Column(name="FACILITY_RRN")
    private Long facilityRrn;

    @Column(name="WAFER_ID")
    private String waferId;

    @Column(name="WAFER_NUM")
    private String waferNum ;

    @Column(name="CST_ID")
    private String cstId;

    /**
     * Frame Qty
     */
    @Column(name="CST_WAFERQTY")
    private String cstWaferqty;

    /**
     * wafer型号
     */
    @Column(name="DEVICE")
    private String device;

    /**
     * second code
     */
    @Column(name="VERSION")
    private String version;

    /**
     * Grade
     */
    @Column(name="WAFER_TYPE")
    private String waferType;

    @Column(name="BOND_PRO")
    private String bondPro;

    @Column(name="SHIP_TO")
    private String shipTo;

    @Column(name="REMARK")
    private String remark;

    @Column(name="VENDER")
    private String vender;

    @Column(name="BOX_ID")
    private String boxId;

    @Column(name="PCODE")
    private String pcode;

    @Column(name="SHIP_OUT_DATE")
    private String shipOutDate;

    @Column(name="PO_NO")
    private String poNo;

    @Column(name="CARTON_NO")
    private String cartonNo;

    @Column(name="WO")
    private String wo;

    @Column(name="INVOICE_NO")
    private String invoiceNo;

    @Column(name="STATUS")
    private String status;

    @Column(name="STOCK_ID")
    private String stockId;

    @Column(name="POINT_ID")
    private String pointId;

    @Column(name="OPERATION_USER")
    private String operationUser;

    @Column(name = "OPERATION_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(timezone = NBUpdatable.GMT_PE, pattern = DateUtils.DEFAULT_DATETIME_PATTERN)
    private Date operationTime = new Date();

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

    @Column(name="WORKORDER_ID")
    private String workorderId;

    @Column(name="STORAGE_LOCATION")
    private String storageLocation;

    public void setMaterialLotUnit(MaterialLotUnit materialLotUnit){
        this.setFacilityRrn(FACILITY_RRN);
        this.setWaferId(materialLotUnit.getUnitId());
        this.setBondPro(materialLotUnit.getReserved4());
        this.setBoxId(materialLotUnit.getMaterialLotId());
        this.setCartonNo(materialLotUnit.getReserved39());
        this.setCstId(materialLotUnit.getDurable());
        this.setCstWaferqty(materialLotUnit.getReserved44());
        this.setDevice(materialLotUnit.getMaterialName());
        this.setDocName(materialLotUnit.getReserved47());
        this.setImportNo(materialLotUnit.getReserved48());
        this.setInvoiceNo(materialLotUnit.getReserved29());
        this.setOperationTime(new Date());
        this.setOperationUser(ThreadLocalContext.getUsername());
        this.setPackageType(PACKAGR_TYPE);
        this.setPcode(materialLotUnit.getReserved3());
        this.setPointId(POINT_ID);
        this.setPoNo(materialLotUnit.getReserved27());
        this.setProdStatus(PROD_STATUS);
        this.setRemark(materialLotUnit.getReserved41());
        this.setShipOutDate(materialLotUnit.getReserved28());
        this.setShipTo(materialLotUnit.getReserved23());
        this.setStatus(WAFER_STATUS_LSWISSUE);
        this.setVender(materialLotUnit.getReserved22());
        this.setVersion(materialLotUnit.getReserved1());
        this.setWaferNum(materialLotUnit.getCurrentQty().toString());
        this.setWaferSource(WAFER_SOURCE);
        this.setWaferType(materialLotUnit.getGrade());
        this.setWo(materialLotUnit.getReserved46());
        this.setWorkorderId(materialLotUnit.getWorkOrderId());
    }

}
