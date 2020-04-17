package com.newbiest.gc.rest.productSubcodeSet;

import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.gc.model.GCProductSubcode;
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
 * Created by guozhangLuo
 */
@RestController
@RequestMapping("/gc")
@Slf4j
@Api(value="/gc", tags="gc客制化接口", description = "产品二级代码设定")
public class ProductSubcodeSetController extends AbstractRestController {

    @Autowired
    GcService gcService;

    @ApiOperation(value = "productSubcodeSetting", notes = "产品二级代码设定")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "ProductSubcodeSetRequest")
    @RequestMapping(value = "/productSubcodeSet", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public ProductSubcodeSetResponse execute(@RequestBody ProductSubcodeSetRequest request) throws Exception {
        ProductSubcodeSetResponse response = new ProductSubcodeSetResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        ProductSubcodeSetResponseBody responseBody = new ProductSubcodeSetResponseBody();

        ProductSubcodeSetRequestBody requestBody = request.getBody();
        GCProductSubcode productSubcode = requestBody.getProductSubcode();

        productSubcode = gcService.saveProductSubcode(productSubcode);

        responseBody.setProductSubcode(productSubcode);
        response.setBody(responseBody);
        return response;
    }

}
