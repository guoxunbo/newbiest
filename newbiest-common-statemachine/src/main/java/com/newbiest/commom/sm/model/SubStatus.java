package com.newbiest.commom.sm.model;

import com.newbiest.base.model.NBUpdatable;
import lombok.Data;

import javax.persistence.*;

/**
 * 状态小类 最小的状态单位
 * Created by guoxunbo on 2017/11/5.
 */
@Entity
@Table(name="COM_SM_SUB_STATUS")
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="CATEGORY", discriminatorType = DiscriminatorType.STRING, length = 32)
@Data
public class SubStatus extends NBUpdatable {

    @Column(name="NAME")
    private String name;

    @Column(name="DESCRIPTION")
    private String description;

}
