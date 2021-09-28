package com.newbiest.mms.model;

import com.newbiest.base.model.NBUpdatable;
import com.newbiest.base.threadlocal.ThreadLocalContext;
import com.newbiest.base.utils.DateUtils;
import com.newbiest.base.utils.StringUtils;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.util.Date;

/**
 * 物料批次当前的检查项
 *
 * @author guoxunbo
 * @date 12/23/20 10:26 AM
 */
@Entity
@Table(name="MMS_MLOT_CHECK_SHEET")
@Data
public class MLotCheckSheet extends NBUpdatable {

    public static final String STATUS_OPEN = "Open";
    public static final String STATUS_CLOSE = "Close";
    public static final String STATUS_IN_APPROVAL = "InApproval";

    @Column(name="MATERIAL_LOT_ID")
    private String materialLotId;

    @Column(name="SHEET_NAME")
    private String sheetName;

    @Column(name="SHEET_DESC")
    private String sheetDesc;

    @Column(name="SHEET_CATEGORY")
    private String sheetCategory;

    @Column(name="CHECK_RESULT")
    private String checkResult;

    @Column(name="CHECK_TIME")
    private Date checkTime;

    @Column(name="CHECK_OWNER")
    private String checkOwner;

    @Column(name="REMARK1")
    private String remark1;

    @Column(name="REMARK2")
    private String remark2;

    @Column(name="STATUS")
    private String status = STATUS_OPEN;

    /**
     * 物料名称
     */
    @Column(name="MATERIAL_NAME")
    private String materialName;

    /**
     * 物料描述
     */
    @Column(name="MATERIAL_DESC")
    private String materialDesc;

    /**
     * 客户版本
     */
    @Column(name="RESERVED1")
    private String reserved1;

    @PreUpdate
    protected void preUpdate() {
        super.preUpdate();
        if (!StringUtils.isNullOrEmpty(checkResult) && MLotCheckSheet.STATUS_IN_APPROVAL.equals(status)) {
            checkTime = DateUtils.now();
            checkOwner = ThreadLocalContext.getUsername();
        }
    }

}
