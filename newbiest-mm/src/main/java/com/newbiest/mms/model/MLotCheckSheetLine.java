package com.newbiest.mms.model;

import com.newbiest.base.model.NBBase;
import com.newbiest.base.model.NBUpdatable;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * 物料批次当前的检查项明细
 *
 * @author guoxunbo
 * @date 12/23/20 10:26 AM
 */
@Entity
@Table(name="MMS_MLOT_CHECK_SHEET_LINE")
@Data
public class MLotCheckSheetLine extends NBUpdatable {

    @Column(name="NAME")
    private String name;

    @Column(name="DESCRIPTION")
    private String description;

    /**
     * MLotCheckSheet的主键
     */
    @Column(name="MLOT_CHECK_SHEET_RRN")
    private Long mLotCheckSheetRrn;

    @Column(name="SHEET_NAME")
    private String sheetName;

    @Column(name="SHEET_DESC")
    private String sheetDesc;

    @Column(name="CHECK_RESULT")
    private String checkResult;

    @Column(name="CHECK_TIME")
    private Date checkTime;

    @Column(name="CHECK_OWNER")
    private String checkOwner;

    @Column(name="ACTION_COMMENT")
    private String actionComment;

}
