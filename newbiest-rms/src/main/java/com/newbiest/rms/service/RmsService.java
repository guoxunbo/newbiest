package com.newbiest.rms.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.rms.model.RecipeEquipment;
import com.newbiest.rms.model.RecipeEquipmentParameter;
import com.newbiest.rms.model.RecipeEquipmentParameterTemp;

import java.util.List;
import java.util.Map;

/**
 * Created by guoxunbo on 2018/7/4.
 */
public interface RmsService {

    RecipeEquipment createRecipeEquipment(RecipeEquipment recipeEquipment) throws ClientException;
    RecipeEquipment activeRecipeEquipment(RecipeEquipment recipeEquipment) throws ClientException;

    void deleteRecipeEquipment(Long recipeEquipmentRrn) throws ClientException;

    void setGoldenRecipe(RecipeEquipment RecipeEquipment) throws ClientException;
    void unSetGoldenRecipe(RecipeEquipment RecipeEquipment, String equipmentId) throws ClientException;

    void recipeOnlineChange(RecipeEquipment recipeEquipment, Map<String, List<String>> contextParameters, List<RecipeEquipmentParameter> changeParameters, String expriedPolicy, int life) throws ClientException;
    void recipeOnlineChange(RecipeEquipment recipeEquipment, List<String> contextParameters, List<RecipeEquipmentParameter> changeParameters, String expriedPolicy, int life) throws ClientException;
    List<RecipeEquipmentParameterTemp> getOnlineRecipe(RecipeEquipment recipeEquipment, List<String> contextParameters) throws ClientException;

    void checkRecipeEquipmentBody(List<RecipeEquipment> recipeEquipmentList, List<String> tempValues, boolean checkHoldState)  throws ClientException;
    void checkRecipeEquipmentBody(RecipeEquipment checkRecipeEquipment, List<String> tempValues, boolean checkHoldState)  throws ClientException;

    RecipeEquipment changeRecipeCheckSum(RecipeEquipment equipmentRecipe, String checkSum) throws ClientException;

    RecipeEquipment downloadRecipe(String lotId, String equipmentId, long recipeEquipmentRrn)  throws ClientException;

}
