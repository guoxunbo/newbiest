package com.newbiest.mms.rest.materiallot.unit;

import com.google.common.collect.Lists;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.mms.model.MaterialLotUnit;
import com.newbiest.mms.service.MaterialLotUnitService;
import com.newbiest.msg.Request;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by guoxunbo on 2018/7/12.
 */
@RestController
@RequestMapping("/mms")
@Slf4j
@Api(value="/mms", tags="MaterialManagerSystem", description = "物料管理相关")
public class MaterialLotUnitController extends AbstractRestController {

    @Autowired
    MaterialLotUnitService materialLotUnitService;

    @ApiOperation(value = "对物料批单元做操作", notes = "接收。消耗。hold/release等")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "MaterialLotUnitRequest")
    @RequestMapping(value = "/materialLotUnitManage", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public MaterialLotUnitResponse execute(@RequestBody MaterialLotUnitRequest request) throws Exception {
        MaterialLotUnitResponse response = new MaterialLotUnitResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        MaterialLotUnitResponseBody responseBody = new MaterialLotUnitResponseBody();

        MaterialLotUnitRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();

        List<MaterialLotUnit> materialLotUnitList;

        if (MaterialLotUnitRequest.ACTION_CREATE.equals(actionType)) {
            materialLotUnitList = materialLotUnitService.createMLot(requestBody.getMaterialLotUnits());
        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }
        responseBody.setMaterialLotUnits(materialLotUnitList);
        response.setBody(responseBody);
        return response;
    }

}
