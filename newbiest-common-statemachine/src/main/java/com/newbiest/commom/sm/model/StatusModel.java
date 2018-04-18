package com.newbiest.commom.sm.model;

import com.newbiest.base.model.NBUpdatable;

import javax.persistence.*;

/**
 * 状态模型基类 Category栏位用来区分不同的StatusModel
 * Created by guoxunbo on 2017/11/5.
 */
@Entity
@Table(name = "COM_STATUS_MODEL")
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="CATEGORY", discriminatorType = DiscriminatorType.STRING, length = 32)
public class StatusModel extends NBUpdatable {

    @Column(name="name")
    private String name;

    @Column(name="DESCRIPTION")
    private String description;

    /**
     * 对象类型
     */
    @Column(name="OBJECT_TYPE")
    private String objectType;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInitialStateCategory() {
        return initialStateCategory;
    }

    public void setInitialStateCategory(String initialStateCategory) {
        this.initialStateCategory = initialStateCategory;
    }

    public String getInitialState() {
        return initialState;
    }

    public void setInitialState(String initialState) {
        this.initialState = initialState;
    }

    public String getInitialSubState() {
        return initialSubState;
    }

    public void setInitialSubState(String initialSubState) {
        this.initialSubState = initialSubState;
    }
}
