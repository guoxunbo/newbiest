package com.newbiest.gc.rest.validateMLotReserved;

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
 *  验证出货真空包信息
 *  扫描的箱信息需要验证箱中所有真空包是否已经备货，备货是否是同一个单号
 */
@RestController
@RequestMapping("/gc")
@Slf4j
@Api(value="/gc", tags="gc客制化接口", description = "GalaxyCore客制化接口")
public class ValidationMLotReservedController extends AbstractRestController {

    @Autowired
    GcService gcService;

    @ApiOperation(value = "ValidationMLotReserved", notes = "验证箱信息")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "ValidationRequest")
    @RequestMapping(value = "/validationMLotReserved", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public ValidationMLotReservedResponse execute(@RequestBody ValidationMLotReservedRequest request)  throws Exception{
        ValidationMLotReservedResponse response = new  ValidationMLotReservedResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        ValidationMLotReservedResponseBody responseBody = new ValidationMLotReservedResponseBody();
        ValidationMLotReservedRequestBody requestBody = request.getBody();

        gcService.validationMLotReserved(requestBody.getMaterialLot());
        response.setBody(responseBody);
        return response;
    }


}
