package com.newbiest.rms.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created by guoxunbo on 2018/7/3.
 */
@Entity
@DiscriminatorValue("RECIPE")
public class RecipeEquipment extends AbstractRecipeEquipment {

    private static final long serialVersionUID = 6091706887024508280L;

}
