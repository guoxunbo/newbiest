package com.newbiest.calendar.model;

import com.newbiest.base.model.NBUpdatable;
import com.newbiest.base.utils.StringUtils;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 交接班->设备委案情况/设备分析
 * Created by guoxunbo on 2019/4/19.
 */
@Entity
@Table(name="DMS_CHANGE_SHIFT_EQP_COMMISSION")
@Data
public class ChangeShiftEqpCommission extends NBUpdatable {

    @Column(name="NAME")
    private String name;

    @Column(name="CHANGE_SHIFT_RRN")
    private Long changeShiftRrn;

    @Column(name="EQUIPMENT_ID")
    private String equipmentId;

    /**
     * 是否急件
     */
    @Column(name="HOT_FLAG")
    private String hotFlag;

    /**
     * 是否完成
     */
    @Column(name="FINISH_FLAG")
    private String finishFlag;

    @Column(name="OWNER")
    private String owner;

    /**
     * 委托部门
     */
    @Column(name="CLIENT_DEPARTMENT")
    private String clientDepartment;

    /**
     * 委托部门
     */
    @Column(name="CLIENT_OWNER")
    private String clientOwner;

    /**
     * 分析项目
     */
    @Column(name="ANALYSE_PROJECT")
    private String analyseProject;

    /**
     * 分析时长(H)
     */
    @Column(name="ANALYSE_DURATION")
    private Integer analyseDuration;


    @Column(name="QTY")
    private Integer qty;

    @Column(name="COMMENT")
    private String comment;

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

    @Column(name="RESERVED10")
    private String reserved10;

    public Boolean getHotFlag() {
        return StringUtils.YES.equalsIgnoreCase(hotFlag);
    }

    public void setHotFlag(Boolean hotFlag) {
        this.hotFlag = hotFlag ? StringUtils.YES : StringUtils.NO;
    }

    public Boolean getFinishFlag() {
        return StringUtils.YES.equalsIgnoreCase(finishFlag);
    }

    public void setFinishFlag(Boolean finishFlag) {
        this.finishFlag = finishFlag ? StringUtils.YES : StringUtils.NO;
    }

}
