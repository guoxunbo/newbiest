package com.newbiest.base.ui.model;

import com.newbiest.base.model.NBBase;
import lombok.Data;

import javax.persistence.*;

/**
 * 参考名称定义
 * Created by guoxunbo on 2018/2/7.
 */
@Entity
@Table(name="NB_REFERENCE_NAME")
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="CATEGORY", discriminatorType = DiscriminatorType.STRING, length = 32)
@Data
public class NBReferenceName extends NBBase {

    private static final long serialVersionUID = -7783610191753111906L;

    public static final String REFERENCE_NAME_LANGUAGE = "Language";

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

}
