package com.newbiest.commom.sm.model;

import com.newbiest.base.model.NBUpdatable;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * 状态模型基类 Category栏位用来区分不同的StatusModel
 * Created by guoxunbo on 2017/11/5.
 */
@Entity
@Table(name = "COM_SM_STATUS_MODEL")
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="CATEGORY", discriminatorType = DiscriminatorType.STRING, length = 32)
@Data
public class StatusModel extends NBUpdatable {

    @Column(name="name")
    private String name;

    @Column(name="DESCRIPTION")
    private String description;

    @Column(name="CATEGORY", insertable=false, updatable=false)
    private String category;

    /**
     * 初始化状态大类
     */
    @Column(name="INITIAL_STATE_CATEGORY")
    private String initialStateCategory;

    /**
     * 初始化状态
     */
    @Column(name="INITIAL_STATE")
    private String initialState;

    /**
     * 初始化状态小类
     */
    @Column(name="INITIAL_SUB_STATE")
    private String initialSubState;

    @ManyToMany(targetEntity = Event.class, fetch=FetchType.LAZY)
    @JoinTable(name = "COM_SM_STATUS_MODEL_EVENT",
            inverseJoinColumns = @JoinColumn(name = "EVENT_RRN", referencedColumnName = "OBJECT_RRN"),
            joinColumns = @JoinColumn(name = "MODEL_RRN", referencedColumnName = "OBJECT_RRN"))
    private List<Event> events;

}
