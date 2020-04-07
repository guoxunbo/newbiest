package com.newbiest.rms.repository.custom;

import com.newbiest.base.exception.ClientException;
import com.newbiest.rms.model.RecipeEquipment;

import java.util.List;


public interface RecipeEquipmentRepositoryCustom {

    RecipeEquipment getDeepRecipeEquipment(long objectRrn) throws ClientException;
    RecipeEquipment getGoldenRecipe(long orgRrn, String eqpType, String recipeName, String status, String pattern, boolean bodyFlag) throws ClientException;
    RecipeEquipment getActiveRecipeEquipment(long orgRrn, String recipeName, String equipmentId, String pattern, boolean bodyFlag) throws ClientException;

}
