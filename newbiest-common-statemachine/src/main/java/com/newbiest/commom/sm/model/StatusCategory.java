package com.newbiest.commom.sm.model;

import com.newbiest.base.model.NBUpdatable;
import lombok.Data;

import javax.persistence.*;

/**
 * 状态大类 一个状态由状态大类+状态+状态小类决定唯一性(状态3层)
 * Created by guoxunbo on 2017/11/5.
 */
@Entity
@Table(name="COM_SM_STATUS_CATEGORY")
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="CATEGORY", discriminatorType = DiscriminatorType.STRING, length = 32)
@Data
public class StatusCategory extends NBUpdatable {

    @Column(name="NAME")
    private String name;

    @Column(name="DESCRIPTION")
    private String description;

}
