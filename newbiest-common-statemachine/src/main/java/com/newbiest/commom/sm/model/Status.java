package com.newbiest.commom.sm.model;

import com.newbiest.base.model.NBUpdatable;

import javax.persistence.*;

/**
 * 状态类 StateCategory+State确定一个状态
 * Created by guoxunbo on 2017/11/5.
 */
@Entity
@Table(name="COM_SM_STATUS")
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="CATEGORY", discriminatorType = DiscriminatorType.STRING, length = 32)
public class Status extends NBUpdatable {

    @Column(name="OBJECT_TYPE")
    private String objectType;

    @Column(name="STATE_CATEGORY")
    private String stateCategory;

    @Column(name="STATE")
    private String state;

    @Column(name="DESCRIPTION")
    private String description;

    @Column(name="AVAILABLE_FLAG")
    private String availableFlag;

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getStateCategory() {
        return stateCategory;
    }

    public void setStateCategory(String stateCategory) {
        this.stateCategory = stateCategory;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getAvailableFlag() {
        return "Y".equalsIgnoreCase(availableFlag);
    }

    public void setAvailableFlag(Boolean availableFlag) {
        this.availableFlag = availableFlag ? "Y" : "N";
    }
}
