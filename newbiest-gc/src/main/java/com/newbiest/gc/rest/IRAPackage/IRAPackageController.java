package com.newbiest.gc.rest.IRAPackage;

import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.gc.service.GcService;
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

@RestController
@RequestMapping("/gc")
@Slf4j
@Api(value="/gc", tags="IRAPackageManager", description = "IRA包装相关")
public class IRAPackageController extends AbstractRestController {

    @Autowired
    GcService gcService;

    @ApiOperation(value = "IRA包装", notes = "IRA包装")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "IraPackageRequest")
    @RequestMapping(value = "/IRAPackageManager", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public IRAPackageResponse execute(@RequestBody IRAPackageRequest request) throws Exception {
        IRAPackageResponse response = new IRAPackageResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        IRAPackageResponseBody responseBody = new IRAPackageResponseBody();

        IRAPackageRequestBody requestBody = request.getBody();
        if (IRAPackageRequestBody.IRA_PACKAGE.equals(requestBody.getPackageType())){
            String packMaterialLotId = gcService.packageIRAs(requestBody.getMaterialLotActions(), requestBody.getPackageType());
            responseBody.setMaterialLotId(packMaterialLotId);
        }
        if (IRAPackageRequestBody.IRA_UN_PACKAGE.equals(requestBody.getPackageType())){
            gcService.unPackageIRAs(requestBody.getMaterialLotActions(), requestBody.getPackageType());
        }
        response.setBody(responseBody);
        return response;
    }

}
