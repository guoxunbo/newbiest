package com.newbiest.commom.sm.model;

import com.newbiest.base.model.NBUpdatable;
import com.newbiest.base.utils.StringUtils;
import lombok.Data;

import javax.persistence.*;

/**
 * 状态类 一个状态由状态大类+状态+状态小类决定唯一性(状态3层)
 * Created by guoxunbo on 2017/11/5.
 */
@Entity
@Table(name="COM_SM_STATUS")
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="CATEGORY", discriminatorType = DiscriminatorType.STRING, length = 32)
@Data
public class Status extends NBUpdatable {

    @Column(name="NAME")
    private String name;

    @Column(name="DESCRIPTION")
    private String description;

    @Column(name="AVAILABLE_FLAG")
    private String availableFlag;

    public Boolean getAvailableFlag() {
        return StringUtils.YES.equalsIgnoreCase(availableFlag);
    }

    public void setAvailableFlag(Boolean availableFlag) {
        this.availableFlag = availableFlag ? StringUtils.YES : StringUtils.NO;
    }
}
