package com.newbiest.commom.sm.model;

import com.newbiest.base.model.NBUpdatable;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 *
 * Created by guoxunbo on 2017/11/5.
 */
@Entity
@Table(name = "COM_SM_EVENT")
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="CATEGORY", discriminatorType = DiscriminatorType.STRING, length = 32)
@Data
public class Event extends NBUpdatable {

    @Column(name="NAME")
    private String name;

    @Column(name="DESCRIPTION")
    private String description;

    @Column(name="CATEGORY", insertable = false, updatable = false)
    private String category;

    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    @JoinColumn(name = "EVENT_RRN", referencedColumnName = "OBJECT_RRN")
    private List<EventStatus> eventStatuses;

}
