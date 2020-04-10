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

    @Column(name="RECIPE_EQUIPMENT_RRN")
    private Long recipeEquipmentRrn;

    @Column(name="RECIPE_NAME")
    private String recipeName;

    @Column(name="EQUIPMENT_ID")
    private String equipmentId;

    @Column(name="VERSION")
    private Long version;

    @Column(name="UNIT_ID")
    private String unitId;

    @Column(name="UNIT_RECIPE_EQUIPMENT_RRN")
    private Long unitRecipeEquipmentRrn;

    @Column(name="UNIT_RECIPE_NAME")
    private String unitRecipeName;

    @Column(name="UNIT_RECIPE_VERSION")
    private Long unitRecipeVersion;

    @Column(name="SEQ_NO")
    private Long seqNo;

}
