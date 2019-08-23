package com.newbiest.mms.rest.pack;

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
 * 物料批次包装
 */
@RestController
@RequestMapping("/mms")
@Slf4j
@Api(value="/mms", tags="MaterialManagerSystem", description = "物料管理相关")
public class PackMaterialLotController extends AbstractRestController {

    @Autowired
    PackageService packageService;

    @ApiOperation(value = "物料批次包装", notes = "物料批次包装")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "PackMaterialLotRequest")
    @RequestMapping(value = "/packMaterialLots", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public PackMaterialLotResponse execute(@RequestBody PackMaterialLotRequest request) throws Exception {
        PackMaterialLotResponse response = new PackMaterialLotResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        PackMaterialLotResponseBody responseBody = new PackMaterialLotResponseBody();

        PackMaterialLotRequestBody requestBody = request.getBody();

        packageService.packageMLots(requestBody.getMaterialLotActions(), requestBody.getPackageType());
        response.setBody(responseBody);
        return response;
    }

}
