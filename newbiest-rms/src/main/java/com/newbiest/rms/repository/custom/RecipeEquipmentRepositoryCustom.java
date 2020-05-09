package com.newbiest.rms.repository.custom;

import com.newbiest.base.exception.ClientException;
import com.newbiest.rms.model.RecipeEquipment;


public interface RecipeEquipmentRepositoryCustom {

//    RecipeEquipment getActiveRecipeEquipment(long orgRrn, String recipeName, String equipmentId, String pattern, boolean bodyFlag) throws ClientException;

    RecipeEquipment getDeepRecipeEquipment(String objectRrn) throws ClientException;
    RecipeEquipment getGoldenRecipe(String eqpType, String recipeName, String status, String pattern, boolean bodyFlag) throws ClientException;

}
