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
    private String mLotCheckSheetRrn;

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

    /**
     * 抽样备注
     */
    @Column(name="SAMPLING_REMARK")
    private String samplingRemark;

    /**
     * 抽样方案
     */
    @Column(name="SAMPLING_SCHEME")
    private String samplingScheme ;

    @PreUpdate
    protected void preUpdate() {
        super.preUpdate();
        if (!StringUtils.isNullOrEmpty(checkResult)) {
            checkTime = DateUtils.now();
            checkOwner = ThreadLocalContext.getUsername();
        }
    }

}
