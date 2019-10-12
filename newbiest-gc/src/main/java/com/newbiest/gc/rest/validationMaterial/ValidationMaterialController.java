package com.newbiest.gc.rest.validationMaterial;

import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.gc.service.GcService;
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
 * 验证出货规则。
 *  扫描的箱信息需要验证二级代码、产品等信息是否相同
 */
@RestController
@RequestMapping("/gc")
@Slf4j
@Api(value="/gc", tags="gc客制化接口", description = "GalaxyCore客制化接口")
public class ValidationMaterialController extends AbstractRestController {

    @Autowired
    GcService gcService;

    @ApiOperation(value = "ValidationMatrial", notes = "验证箱信息")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "ValidationRequest")
    @RequestMapping(value = "/validationMaterial", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public ValidationMaterialResponse execute(@RequestBody ValidationMaterialRequest request) throws Exception {
        ValidationMaterialResponse response = new ValidationMaterialResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        ValidationMaterialResponseBody responseBody = new ValidationMaterialResponseBody();

        ValidationMaterialRequestBody requestBody = request.getBody();
        gcService.validationMaterial(requestBody.getMaterialLotFirst(), requestBody.getMaterialLot());
        response.setBody(responseBody);
        return response;
    }
}
