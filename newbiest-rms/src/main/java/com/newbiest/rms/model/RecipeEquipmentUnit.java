package com.newbiest.rms.model;

import com.newbiest.base.model.NBUpdatable;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


@Entity
@Table(name="RMS_RECIPE_EQUIPMENT_UNIT")
@Data
public class RecipeEquipmentUnit extends NBUpdatable {

    private static final long serialVersionUID = 1L;

    @Column(name="EQP_RECIPE_RRN")
    private Long recipeEquipmentRrn;

    @Column(name="RECIPE_NAME")
    private Long recipeName;

    @Column(name="EQUIPMENT_ID")
    private String equipmentId;

    @Column(name="EQUIPMENT_TYPE")
    private String equipmentType;

    @Column(name="EQP_RECIPE_VERSION")
    private Long recipeEquipmentVersion;

    @Column(name="PATTERN")
    private String pattern;

    @Column(name="UNIT_RECIPE_RRN")
    private Long unitRecipeRrn;

    @Column(name="UNIT_RECIPE_NAME")
    private String unitRecipeName;

    @Column(name="UNIT_ID")
    private String unitId;

    @Column(name="UNIT_TYPE")
    private String unitType;

    @Column(name="UNIT_RECIPE_VERSION")
    private Long unitRecipeVersion;

    @Column(name="UNIT_SEQ")
    private Long unitSeq;

}
