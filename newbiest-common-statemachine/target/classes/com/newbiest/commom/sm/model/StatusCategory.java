package com.newbiest.commom.sm.model;

import com.newbiest.base.model.NBUpdatable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 状态大类 一个状态由状态大类+状态+状态小类决定唯一性(状态3层)
 * Created by guoxunbo on 2017/11/5.
 */
@Entity
@Table(name="COM_SM_STATUS_CATEGORY")
public class StatusCategory extends NBUpdatable {

    @Column(name="OBJECT_TYPE")
    private String objectType;

    @Column(name="STATUS_CATEGORY")
    private String statusCategory;

    @Column(name="DESCRIPTION")
    private String description;

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getStatusCategory() {
        return statusCategory;
    }

    public void setStatusCategory(String statusCategory) {
        this.statusCategory = statusCategory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
