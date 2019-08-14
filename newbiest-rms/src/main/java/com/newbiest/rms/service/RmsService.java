package com.newbiest.rms.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.rms.model.AbstractRecipeEquipment;
import com.newbiest.rms.model.RecipeEquipmentParameter;
import com.newbiest.rms.model.RecipeEquipmentParameterTemp;

import java.util.List;
import java.util.Map;

/**
 * Created by guoxunbo on 2018/7/4.
 */
public interface RmsService {

    AbstractRecipeEquipment saveRecipeEquipment(AbstractRecipeEquipment recipeEquipment) throws ClientException;
    void deleteRecipeEquipment(Long recipeEquipmentRrn) throws ClientException;
    AbstractRecipeEquipment frozenRecipeEquipment(AbstractRecipeEquipment recipeEquipment) throws ClientException;
    AbstractRecipeEquipment unFrozenRecipeEquipment(AbstractRecipeEquipment recipeEquipment) throws ClientException;
    AbstractRecipeEquipment activeRecipeEquipment(AbstractRecipeEquipment recipeEquipment,  boolean isActiveGloden, boolean sendNotification) throws ClientException;
    AbstractRecipeEquipment inActiveRecipeEquipment(AbstractRecipeEquipment recipeEquipment, boolean checkGoldenFlag) throws ClientException;

    void holdRecipeEquipment(AbstractRecipeEquipment abstractRecipeEquipment, String actionCode, String actionReason, String actionComment) throws ClientException;
    void releaseRecipeEquipment(AbstractRecipeEquipment abstractRecipeEquipment, String actionCode, String actionReason, String actionComment) throws ClientException;

    void setGoldenRecipe(AbstractRecipeEquipment abstractRecipeEquipment) throws ClientException;
    void unSetGoldenRecipe(AbstractRecipeEquipment abstractRecipeEquipment, String equipmentId) throws ClientException;

    void recipeOnlineChange(AbstractRecipeEquipment recipeEquipment, Map<String, List<String>> contextParameters, List<RecipeEquipmentParameter> changeParameters, String expriedPolicy, int life) throws ClientException;
    void recipeOnlineChange(AbstractRecipeEquipment recipeEquipment, List<String> contextParameters, List<RecipeEquipmentParameter> changeParameters, String expriedPolicy, int life) throws ClientException;
    List<RecipeEquipmentParameterTemp> getOnlineRecipe(AbstractRecipeEquipment recipeEquipment, List<String> contextParameters) throws ClientException;

    void checkRecipeEquipmentBody(List<AbstractRecipeEquipment> recipeEquipmentList, List<String> tempValues, boolean checkHoldState)  throws ClientException;
    void checkRecipeEquipmentBody(AbstractRecipeEquipment checkRecipeEquipment, List<String> tempValues, boolean checkHoldState)  throws ClientException;

    AbstractRecipeEquipment changeRecipeCheckSum(AbstractRecipeEquipment equipmentRecipe, String checkSum) throws ClientException;

    AbstractRecipeEquipment downloadRecipe(String lotId, String equipmentId, long recipeEquipmentRrn)  throws ClientException;

}
