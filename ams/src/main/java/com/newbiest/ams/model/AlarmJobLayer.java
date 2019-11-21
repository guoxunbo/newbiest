package com.newbiest.ams.model;

import com.newbiest.ams.action.AlarmAction;
import com.newbiest.base.model.NBBase;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Job处理的层级关系。允许向上汇报
 * seqNo越小，越优先汇报
 * Created by guoxunbo on 2019-11-19 16:08
 */
@Data
@Entity
@Table(name="AMS_JOB_LAYER")
public class AlarmJobLayer extends NBBase {

    @Column(name="JOB_RRN")
    private String jobRrn;

    /**
     * 序号
     * 允许相同序号，相同序号同时处理
     */
    @Column(name="SEQ_NO")
    private Long seqNo;

    @Column(name="ACTION_TYPE")
    private String actionType;

    /**
     * 目标用户
     */
    @Column(name="TO_USER")
    private String toUser;

    /**
     * 目标用户组
     */
    @Column(name="TO_ROLE")
    private String toRole;

    /**
     * 没被确认前，是否重复发送
     */
    @Column(name="ACTION_TYPE")
    private String repeatFlag;

    /**
     * 重复发送时间间隔，单位分钟
     */
    @Column(name="ACTION_TYPE")
    private String repeatInterval;

    public AlarmAction createAction() {
        return null;
    }

}
