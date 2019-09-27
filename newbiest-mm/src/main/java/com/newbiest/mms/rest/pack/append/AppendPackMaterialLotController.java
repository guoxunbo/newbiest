package com.newbiest.mms.rest.pack.append;

import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.service.PackageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 物料批次追加包装
 */
@RestController
@RequestMapping("/mms")
@Slf4j
@Api(value="/mms", tags="MaterialManagerSystem", description = "物料管理相关")
public class AppendPackMaterialLotController extends AbstractRestController {

    @Autowired
    PackageService packageService;

    @ApiOperation(value = "追加包装", notes = "追加包装")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "AppendPackMaterialLotRequest")
    @RequestMapping(value = "/appendPackMaterialLots", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public AppendPackMaterialLotResponse execute(@RequestBody AppendPackMaterialLotRequest request) throws Exception {
        AppendPackMaterialLotResponse response = new AppendPackMaterialLotResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        AppendPackMaterialLotResponseBody responseBody = new AppendPackMaterialLotResponseBody();

        AppendPackMaterialLotRequestBody requestBody = request.getBody();

        MaterialLot appendMaterialLot = packageService.appendPacking(requestBody.getPackedMaterialLot(), requestBody.getWaitToAppendActions());
        responseBody.setMaterialLot(appendMaterialLot);
        response.setBody(responseBody);
        return response;
    }

}
