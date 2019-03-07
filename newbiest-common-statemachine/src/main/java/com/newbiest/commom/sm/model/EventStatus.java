package com.newbiest.commom.sm.model;

import com.newbiest.base.model.NBBase;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 事件状态关联类(触发事件状态的变更)
 * Created by guoxunbo on 2017/11/5.
 */
@Entity
@Table(name="COM_SM_EVENT_STATUS")
@Data
public class EventStatus extends NBBase {

    /**
     * 拒绝此状态变更
     */
    public static String CHECK_FLAG_REJECT = "Reject";
    /**
     * 允许此状态变更
     */
    public static String CHECK_FLAG_ALLOW = "Allow";

    /**
     * 表示所有的状态
     */
    public static String ALL_FLAG = "*";

    @Column(name="EVENT_RRN")
    private Long eventRrn;

    @Column(name="CHECK_FLAG")
    private String checkFlag;

    /**
     * 源状态大类
     */
    @Column(name="SOURCE_STATUS_CATEGORY")
    private String sourceStatusCategory;

    /**
     * 源状态
     */
    @Column(name="SOURCE_STATUS")
    private String sourceStatus;

    /**
     * 源状态小类
     */
    @Column(name="SOURCE_SUB_STATUS")
    private String sourceSubStatus = ALL_FLAG;

    /**
     * 目标状态大类
     */
    @Column(name="TARGET_STATUS_CATEGORY")
    private String targetStatusCategory;

    /**
     * 目标状态
     */
    @Column(name="TARGET_STATUS")
    private String targetStatus;

    /**
     * 目标状态小类
     */
    @Column(name="TARGET_SUB_STATUS")
    private String targetSubStatus = ALL_FLAG;

}
