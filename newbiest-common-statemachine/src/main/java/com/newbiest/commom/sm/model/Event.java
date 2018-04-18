package com.newbiest.commom.sm.model;

import com.newbiest.base.model.NBUpdatable;

import javax.persistence.*;
import java.util.List;

/**
 *
 * Created by guoxunbo on 2017/11/5.
 */
@Entity
@Table(name = "COM_STATUS_EVENT")
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="CATEGORY", discriminatorType = DiscriminatorType.STRING, length = 32)
public class Event extends NBUpdatable {

    @Column(name="EVENT_ID")
    private String eventId;

    @Column(name="DESCRIPTION")
    private String description;

    @Column(name="OBJECT_TYPE")
    private String objectType;

    @Column(name="EVENT_TYPE")
    private String eventType;

    @Column(name="CATEGORY", insertable = false, updatable = false)
    private String category;

    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "EVENT_RRN", referencedColumnName = "OBJECT_RRN")
    private List<EventStatus> eventStatus;

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<EventStatus> getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(List<EventStatus> eventStatus) {
        this.eventStatus = eventStatus;
    }
}
