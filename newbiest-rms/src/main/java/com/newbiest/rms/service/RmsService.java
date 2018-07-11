package com.newbiest.rms.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.rms.model.AbstractRecipeEquipment;
import com.newbiest.rms.model.RecipeEquipmentParameter;
import com.newbiest.rms.model.RecipeEquipmentParameterTemp;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by guoxunbo on 2018/7/4.
 */
public interface RmsService {

    AbstractRecipeEquipment saveRecipeEquipment(AbstractRecipeEquipment recipeEquipment, SessionContext sc) throws ClientException;
    void deleteRecipeEquipment(Long recipeEquipmentRrn, SessionContext sc) throws ClientException;
    AbstractRecipeEquipment frozenRecipeEquipment(AbstractRecipeEquipment recipeEquipment, SessionContext sc) throws ClientException;
    AbstractRecipeEquipment unFrozenRecipeEquipment(AbstractRecipeEquipment recipeEquipment, SessionContext sc) throws ClientException;
    AbstractRecipeEquipment activeRecipeEquipment(AbstractRecipeEquipment recipeEquipment,  boolean isActiveGloden, boolean sendNotification, SessionContext sc) throws ClientException;
    AbstractRecipeEquipment inActiveRecipeEquipment(AbstractRecipeEquipment recipeEquipment, boolean checkGolenFlag, SessionContext sc) throws ClientException;

    void holdRecipeEquipment(AbstractRecipeEquipment abstractRecipeEquipment, String actionCode, String actionReason, String actionComment, SessionContext sc) throws ClientException;
    void releaseRecipeEquipment(AbstractRecipeEquipment abstractRecipeEquipment, String actionCode, String actionReason, String actionComment, SessionContext sc) throws ClientException;

    void setGoldenRecipe(AbstractRecipeEquipment abstractRecipeEquipment, SessionContext sc) throws ClientException;
    void unSetGoldenRecipe(AbstractRecipeEquipment abstractRecipeEquipment, String equipmentId, SessionContext sc) throws ClientException;

    void recipeOnlineChange(AbstractRecipeEquipment recipeEquipment, Map<String, List<String>> contextParameters, List<RecipeEquipmentParameter> changeParameters, String expriedPolicy, int life, SessionContext sc) throws ClientException;
    void recipeOnlineChange(AbstractRecipeEquipment recipeEquipment, List<String> contextParameters, List<RecipeEquipmentParameter> changeParameters, String expriedPolicy, int life, SessionContext sc) throws ClientException;
    List<RecipeEquipmentParameterTemp> getOnlineRecipe(AbstractRecipeEquipment recipeEquipment, List<String> contextParameters, SessionContext sc) throws ClientException;

    void checkRecipeEquipmentBody(List<AbstractRecipeEquipment> recipeEquipmentList, List<String> tempValues, boolean checkHoldState, SessionContext sc)  throws ClientException;
    void checkRecipeEquipmentBody(AbstractRecipeEquipment checkRecipeEquipment, List<String> tempValues, boolean checkHoldState, SessionContext sc)  throws ClientException;

    AbstractRecipeEquipment changeRecipeCheckSum(AbstractRecipeEquipment equipmentRecipe, String checkSum, SessionContext sc) throws ClientException;

    AbstractRecipeEquipment downloadRecipe(String lotId, String equipmentId, long recipeEquipmentRrn, SessionContext sc)  throws ClientException;

}
