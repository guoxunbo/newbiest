package com.newbiest.vanchip.rest.product;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.msg.Request;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.commom.sm.exception.StatusMachineExceptions;
import com.newbiest.mms.model.Material;
import com.newbiest.mms.model.Product;
import com.newbiest.mms.repository.MaterialStatusModelRepository;
import com.newbiest.mms.state.model.MaterialStatusModel;
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

import java.util.List;

@RestController
@RequestMapping("/vc")
@Slf4j
@Api(value="/vc", tags="vc客制化", description = "物料管理相关")
public class ProductController extends AbstractRestController {

    @Autowired
    VanChipService vanChipService;

    @Autowired
    MaterialStatusModelRepository materialStatusModelRepository;

    @ApiOperation(value = "对成品物料做操作", notes = "成品当前不考虑版本信息")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "ProductRequest")
    @RequestMapping(value = "/productManager", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public ProductResponse execute(@RequestBody ProductRequest request) throws Exception {
        ProductResponse response = new ProductResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        ProductResponseBody responseBody = new ProductResponseBody();

        ProductRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();
        List<Product> products = requestBody.getDataList();

        if (ProductRequest.ACTION_IMPORT_SAVE.equals(actionType)){
            MaterialStatusModel statusModel = materialStatusModelRepository.findOneByName(Material.DEFAULT_STATUS_MODEL);
            if (statusModel == null) {
                throw new ClientException(StatusMachineExceptions.STATUS_MODEL_IS_NOT_EXIST);
            }

            for (Product product: products){
                product.setStatusModelRrn(statusModel.getObjectRrn());
                vanChipService.saveProduct(product);
            }
        }else if (ProductRequest.ACTION_MERGE.equals(actionType)){
            Product product = products.get(0);
            product = vanChipService.saveProduct(product);
            responseBody.setMaterial(product);
        }else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }
        response.setBody(responseBody);
        return response;
    }

}
