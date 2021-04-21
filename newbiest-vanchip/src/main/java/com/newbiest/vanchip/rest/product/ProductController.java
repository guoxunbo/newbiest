package com.newbiest.vanchip.rest.product;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.msg.Request;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.mms.exception.MmsException;
import com.newbiest.mms.model.Material;
import com.newbiest.mms.model.Product;
import com.newbiest.mms.service.MmsService;
import com.newbiest.vanchip.service.VanChipService;
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
@RequestMapping("/vc")
@Slf4j
@Api(value="/vc", tags="vc客制化", description = "物料管理相关")
public class ProductController extends AbstractRestController {

    @Autowired
    MmsService mmsService;

    @Autowired
    VanChipService vanChipService;

    @ApiOperation(value = "对成品物料做操作", notes = "成品当前不考虑版本信息")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "ProductRequest")
    @RequestMapping(value = "/productManager", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public ProductResponse execute(@RequestBody ProductRequest request) throws Exception {
        ProductResponse response = new ProductResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        ProductResponseBody responseBody = new ProductResponseBody();

        ProductRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();
        Product material = requestBody.getMaterial();

        if (ProductRequest.ACTION_CREATE.equals(actionType)) {
            Material oldData = mmsService.getProductByName(material.getName());
            if (oldData != null) {
                throw new ClientParameterException(MmsException.MM_PRODUCT_IS_EXIST, material.getName());
            }
            material = mmsService.saveProduct(material);
        }else if (ProductRequest.ACTION_MERGE.equals(actionType)){
            material = vanChipService.saveProduct(requestBody.getMaterial());
            responseBody.setMaterial(material);
        }else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }
        responseBody.setMaterial(material);
        response.setBody(responseBody);
        return response;
    }

}
