package com.newbiest.rms.rest.eqp;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.msg.Request;
import com.newbiest.base.rest.AbstractRestController;
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
public class EquipmentController extends AbstractRestController {

    @Autowired
    RmsService rmsService;

    @ApiOperation(value = "设备管理", notes = "设备管理，比如Hold/Release以及相应")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "EquipmentRequest")
    @RequestMapping(value = "/equipmentManager", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public EquipmentResponse execute(@RequestBody EquipmentRequest request) throws Exception {
        EquipmentResponse response = new EquipmentResponse();
        EquipmentResponseBody responseBody = new EquipmentResponseBody();

        EquipmentRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();
        if (EquipmentRequest.ACTION_GET_RECIPE_LIST.equals(actionType)) {
            //TODO
        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }

        response.setBody(responseBody);
        return response;
    }

}
