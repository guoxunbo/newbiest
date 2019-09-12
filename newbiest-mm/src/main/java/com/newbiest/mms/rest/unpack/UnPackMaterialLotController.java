package com.newbiest.mms.rest.unpack;

import com.newbiest.base.rest.AbstractRestController;
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
 * 物料批次拆包装
 */
@RestController
@RequestMapping("/mms")
@Slf4j
@Api(value="/mms", tags="MaterialManagerSystem", description = "物料管理相关")
public class UnPackMaterialLotController extends AbstractRestController {

    @Autowired
    PackageService packageService;

    @ApiOperation(value = "物料批次拆包", notes = "物料批次拆包")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "UnPackMaterialLotRequest")
    @RequestMapping(value = "/unPackMaterialLots", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public UnPackMaterialLotResponse execute(@RequestBody UnPackMaterialLotRequest request) throws Exception {
        UnPackMaterialLotResponse response = new UnPackMaterialLotResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        UnPackMaterialLotResponseBody responseBody = new UnPackMaterialLotResponseBody();
        UnPackMaterialLotRequestBody requestBody = request.getBody();

        packageService.unPack(requestBody.getMaterialLotActions());
        response.setBody(responseBody);
        return response;
    }

}
