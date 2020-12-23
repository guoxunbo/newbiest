package com.newbiest.mms.model;

import com.newbiest.base.model.NBBase;
import com.newbiest.base.model.NBUpdatable;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
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

}
