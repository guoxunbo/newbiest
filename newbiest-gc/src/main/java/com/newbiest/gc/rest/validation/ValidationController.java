package com.newbiest.gc.rest.validation;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.gc.service.GcService;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.MaterialLotPackageType;
import com.newbiest.mms.service.MmsService;
import com.newbiest.mms.service.PackageService;
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
 * 验证出货和重测发料的规则。
 *  因为当前2个规则都一样。所以就只用同一个接口
 */
@RestController
@RequestMapping("/gc")
@Slf4j
@Api(value="/gc", tags="gc客制化接口", description = "GalaxyCore客制化接口")
public class ValidationController extends AbstractRestController {

    @Autowired
    GcService gcService;

    @ApiOperation(value = "ValidationSoOrReTest", notes = "验证出货以及发料")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "ValidationRequest")
    @RequestMapping(value = "/validationSoOrReTest", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public ValidationResponse execute(@RequestBody ValidationRequest request) throws Exception {
        ValidationResponse response = new ValidationResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        ValidationResponseBody responseBody = new ValidationResponseBody();

        ValidationRequestBody requestBody = request.getBody();
        gcService.validationDocLine(requestBody.getDocumentLine(), requestBody.getMaterialLot());
        response.setBody(responseBody);
        return response;
    }

}
