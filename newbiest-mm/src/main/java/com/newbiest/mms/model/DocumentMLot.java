package com.newbiest.mms.model;

import com.newbiest.base.model.NBBase;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 单据绑定的物料批次
 *  单据指定物料批次进行绑定/预留
 * @author guoxunbo
 */
@Data
@Entity
@Table(name = "MMS_DOCUMENT_MLOT")
public class DocumentMLot extends NBBase {

    public static final String STATUS_CREATE = "Create";
    public static final String STATUS_ISSUE = "Issue";
    public static final String STATUS_RETURN = "Return";
    public static final String STATUS_RECEIVE = "Receive";
    public static final String STATUS_STOCK_OUT = "StockOut";
    public static final String STATUS_SCRAP = "Scrap";

    public static final String STATUS_CHECK_CREATE = "CheckCreate";
    public static final String STATUS_CHECK = "Check";
    public static final String STATUS_WAIT_RECHECK = "WaitRecheck";
    public static final String STATUS_RECHECK = "Recheck";

    /**
     * 当前的批次没有被发料 而是其子批进行发料
     */
    public static final String STATUS_ISSUE_SUB_MLOT = "IssueSubMLot";

    @Column(name="DOCUMENT_ID")
    private String documentId;

    @Column(name="MATERIAL_LOT_ID")
    private String materialLotId;

    @Column(name="STATUS")
    private String status;

    /**
     * 行号
     */
    @Column(name="ITEM_ID")
    private String itemId;
}
