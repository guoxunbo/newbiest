package com.newbiest.commom.sm.model;

import com.newbiest.base.model.NBUpdatable;
import com.newbiest.security.model.NBRole;

import javax.persistence.*;
import java.util.List;

/**
 * 状态允许触发的事件
 * Created by guoxunbo on 2017/11/5.
 */
@Entity
@Table(name="COM_SM_STATUS_MODEL_EVENT")
public class StatusModelEvent extends NBUpdatable{

    @Column(name="MODEL_RRN")
    private Long modelRrn;

    @Column(name="SEQ_NO")
    private Long seqNo;

    @Column(name="LIMIT_COUNT")
    private Long limitCount;

    @OneToOne(targetEntity = Event.class, fetch=FetchType.EAGER)
    @JoinColumn(name = "EVENT_RRN", referencedColumnName = "OBJECT_RRN", insertable = false, updatable = false)
    private Event event;

    /**
     * 操作此事件的权限
     */
    @ManyToMany(targetEntity = NBRole.class, fetch=FetchType.EAGER)
    @JoinTable(name = "COM_SM_STATUS_MODEL_EVENT_ROLE",
            inverseJoinColumns = @JoinColumn(name = "ROLE_RRN", referencedColumnName = "OBJECT_RRN"),
            joinColumns = @JoinColumn(name = "MODEL_EVENT_RRN", referencedColumnName = "OBJECT_RRN"))
    private List<NBRole> roles;

    public Long getModelRrn() {
        return modelRrn;
    }

    public void setModelRrn(Long modelRrn) {
        this.modelRrn = modelRrn;
    }

    public Long getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(Long seqNo) {
        this.seqNo = seqNo;
    }

    public Long getLimitCount() {
        return limitCount;
    }

    public void setLimitCount(Long limitCount) {
        this.limitCount = limitCount;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public List<NBRole> getRoles() {
        return roles;
    }

    public void setRoles(List<NBRole> roles) {
        this.roles = roles;
    }

    private Long getEventRrn() {
        return event != null ? event.getObjectRrn(): null;
    }

    public String getEventId() {
        return event != null ? event.getEventId() : "";
    }

}
