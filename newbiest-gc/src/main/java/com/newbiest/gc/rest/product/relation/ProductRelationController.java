package com.newbiest.gc.rest.product.relation;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.gc.model.GCProductNumberRelation;
import com.newbiest.gc.service.GcService;
import com.newbiest.msg.Request;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by guozhangLuo
 */
@RestController
@RequestMapping("/gc")
@Slf4j
@Api(value="/gc", tags="gc客制化接口", description = "产品关系绑定")
public class ProductRelationController extends AbstractRestController {

    @Autowired
    GcService gcService;

    @ApiOperation(value = "productRelation", notes = "产品关系绑定")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "ProductRelationRequest")
    @RequestMapping(value = "/productRelation", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public ProductRelationResponse execute(@RequestBody ProductRelationRequest request) throws Exception {
        ProductRelationResponse response = new ProductRelationResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        ProductRelationResponseBody responseBody = new ProductRelationResponseBody();

        ProductRelationRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();
        GCProductNumberRelation productNumberRelation = requestBody.getProductNumberRelation();

        if(ProductRelationRequest.ACTION_TYPE_SAVE_PRODUCT_NUMBER_RELATION.equals(actionType)){
            productNumberRelation = gcService.saveProductNumberRelation(productNumberRelation, GCProductNumberRelation.TRANS_TYPE_CREATE);
            responseBody.setProductNumberRelation(productNumberRelation);
        } else if(ProductRelationRequest.ACTION_TYPE_UPDATE_PRODUCT_NUMBER_RELATION.equals(actionType)){
            productNumberRelation = gcService.saveProductNumberRelation(productNumberRelation, GCProductNumberRelation.TRANS_TYPE_UPDATE);
            responseBody.setProductNumberRelation(productNumberRelation);
        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + actionType);
        }
        response.setBody(responseBody);
        return response;
    }

}
