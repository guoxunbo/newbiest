package com.newbiest.rms.rest.eqp.recipe;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.msg.Request;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.rms.model.RecipeEquipment;
import com.newbiest.rms.service.RmsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rms")
@Slf4j
@Api(value="/rms", tags="RecipeManagerSystem")
public class EqpRecipeController extends AbstractRestController {

    @Autowired
    RmsService rmsService;

    @ApiOperation(value = "设备Recipe管理")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "EqpRecipeRequest")
    @RequestMapping(value = "/eqpRecipeManage", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public EqpRecipeResponse execute(@RequestBody EqpRecipeRequest request) throws Exception {
        EqpRecipeResponse response = new EqpRecipeResponse();
        EqpRecipeResponseBody responseBody = new EqpRecipeResponseBody();

        EqpRecipeRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();
        RecipeEquipment recipeEquipment = requestBody.getRecipeEquipment();

        if (EqpRecipeRequest.ACTION_CREATE.equals(actionType)) {
            recipeEquipment = rmsService.createRecipeEquipment(recipeEquipment);
        } else if (EqpRecipeRequest.ACTION_UPDATE.equals(actionType)) {
            validateEntity(recipeEquipment);
            recipeEquipment = (RecipeEquipment) baseService.saveEntity(recipeEquipment);
        } else if (EqpRecipeRequest.ACTION_TYPE_ACTIVE.equals(actionType)) {
            validationVersionControl(recipeEquipment);
            recipeEquipment = rmsService.activeRecipeEquipment(recipeEquipment);
        } else if (EqpRecipeRequest.ACTION_DELETE.equals(actionType)) {
            rmsService.deleteRecipeEquipment(recipeEquipment.getObjectRrn());
        } else if (EqpRecipeRequest.ACTION_TYPE_SET_GOLDEN.equals(actionType)) {

        } else if (EqpRecipeRequest.ACTION_TYPE_UNSET_GOLDEN.equals(actionType)) {

        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }

        responseBody.setRecipeEquipment(recipeEquipment);
        response.setBody(responseBody);
        return response;
    }

}
