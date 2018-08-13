package com.newbiest.commom.sm.model;

import com.newbiest.base.model.NBUpdatable;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.security.model.NBRole;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * 状态允许触发的事件
 * Created by guoxunbo on 2017/11/5.
 */
@Entity
@Table(name="COM_SM_STATUS_MODEL_EVENT")
@Data
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

    private Long getEventRrn() {
        return event != null ? event.getObjectRrn() : null;
    }

    public String getEventId() {
        return event != null ? event.getEventId() : StringUtils.EMPTY;
    }

}
