package com.newbiest.rms.model;

import com.newbiest.base.model.NBBase;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by guoxunbo on 2018/7/3.
 */
@Entity
@Table(name="RMS_RECIPE")
@Data
public class Recipe extends NBBase {

    public static final String RECIPE_CATEGORY_ABSTRACT = "Abstract";
    public static final String RECIPE_CATEGORY_EQUIPMENT = "Equipment";

    @Column(name="NAME")
    private String name;

    @Column(name="DESCRIPTION")
    private String description;

    @Column(name="CATEGORY")
    private String category;

}
