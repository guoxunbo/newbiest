package com.newbiest.commom.sm.model;

import com.newbiest.base.model.NBBase;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 事件状态关联类(触发事件状态的变更)
 * Created by guoxunbo on 2017/11/5.
 */
@Entity
@Table(name="COM_SM_EVENT_STATUS")
public class EventStatus extends NBBase {

    /**
     * 拒绝此状态变更
     */
    public static String CHECKFLAG_REJECT = "Reject";
    /**
     * 允许此状态变更
     */
    public static String CHECKFLAG_ALLOW = "Allow";

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
    @Column(name="SOURCE_STATE")
    private String sourceState;

    /**
     * 源状态小类
     */
    @Column(name="SOURCE_SUB_STATE")
    private String sourceSubState;

    /**
     * 目标状态大类
     */
    @Column(name="TARGET_STATUS_CATEGORY")
    private String targetStatusCategory;

    /**
     * 目标状态
     */
    @Column(name="TARGET_STATE")
    private String targetState;

    /**
     * 目标状态小类
     */
    @Column(name="TARGET_SUB_STATE")
    private String targetSubState;

    public Long getEventRrn() {
        return eventRrn;
    }

    public void setEventRrn(Long eventRrn) {
        this.eventRrn = eventRrn;
    }

    public String getCheckFlag() {
        return checkFlag;
    }

    public void setCheckFlag(String checkFlag) {
        this.checkFlag = checkFlag;
    }

    public String getSourceStatusCategory() {
        return sourceStatusCategory;
    }

    public void setSourceStatusCategory(String sourceStatusCategory) {
        this.sourceStatusCategory = sourceStatusCategory;
    }

    public String getSourceState() {
        return sourceState;
    }

    public void setSourceState(String sourceState) {
        this.sourceState = sourceState;
    }

    public String getSourceSubState() {
        return sourceSubState;
    }

    public void setSourceSubState(String sourceSubState) {
        this.sourceSubState = sourceSubState;
    }

    public String getTargetStatusCategory() {
        return targetStatusCategory;
    }

    public void setTargetStatusCategory(String targetStatusCategory) {
        this.targetStatusCategory = targetStatusCategory;
    }

    public String getTargetState() {
        return targetState;
    }

    public void setTargetState(String targetState) {
        this.targetState = targetState;
    }

    public String getTargetSubState() {
        return targetSubState;
    }

    public void setTargetSubState(String targetSubState) {
        this.targetSubState = targetSubState;
    }
}
