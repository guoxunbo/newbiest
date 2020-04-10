package com.newbiest.rms.model;

import com.newbiest.base.model.NBBase;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="RMS_RECIPE")
@Data
public class Recipe extends NBBase {

    /**
     * LogicRecipe
     *  使用场景是，一个LogicRecipe对应RecipeEquipment下会有N多unitRecipe。
     *  类似一个线体设备的概念。比较的实际是里面的unitRecipe
     */
    public static final String RECIPE_CATEGORY_Logic = "Logic";

    public static final String RECIPE_CATEGORY_EQUIPMENT = "Equipment";

    @Column(name="NAME")
    private String name;

    @Column(name="DESCRIPTION")
    private String description;

    @Column(name="CATEGORY")
    private String category = RECIPE_CATEGORY_EQUIPMENT;

}
