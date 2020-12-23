package com.newbiest.mms.model;

import com.newbiest.base.model.NBBase;
import lombok.Data;

import javax.persistence.*;

/**
 * 检查项定义
 * @author guoxunbo
 * @date 12/23/20 10:41 AM
 */
@Table(name="MMS_CHECK_SHEET")
@Entity
@Data
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="CATEGORY", discriminatorType = DiscriminatorType.STRING, length = 32)
public class CheckSheet extends NBBase {

    public static final String CATEGORY_IQC = "IQC";
    public static final String CATEGORY_OQC = "OQC";

    @Column(name="NAME")
    private String name;

    @Column(name="DESCRIPTION")
    private String description;

    @Column(name="CATEGORY",insertable = false, updatable = false)
    private String category;

}
