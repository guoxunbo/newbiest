package com.newbiest.vanchip.rest.erp.material;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.msg.Request;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.base.service.BaseService;
import com.newbiest.mms.model.Material;
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

import java.util.List;

@RestController
@RequestMapping("/erp")
@Slf4j
@Api(value="/erp", tags="VanChip客制化ERP接口", description = "ERP物料主数据接口")
public class ErpMaterialController extends AbstractRestController {

    @Autowired
    VanChipService vanChipService;

    @Autowired
    BaseService baseService;

    @Autowired
    MmsService mmsService;

    @ApiOperation(value = "ERP物料主数据Create/Update/Delete")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "ErpMaterialRequest")
    @RequestMapping(value = "/materialManager", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public ErpMaterialResponse execute(@RequestBody ErpMaterialRequest request) throws Exception {
        ErpMaterialResponse response = new ErpMaterialResponse();
        ErpMaterialResponseBody responseBody = new ErpMaterialResponseBody();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());

        String actionType = request.getBody().getActionType();
        List<Material> materialList = request.getBody().getMaterialList();

        if (Request.ACTION_CREATE.equals(actionType)){
            vanChipService.erpSaveMaterial(materialList);
        }else if (Request.ACTION_UPDATE.equals(actionType)){
            vanChipService.erpSaveMaterial(materialList);
        }else if (Request.ACTION_DELETE.equals(actionType)){
            //vanchip客制化del不处理。
        }else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + actionType);
        }
        response.setBody(responseBody);
        return response;
    }
}
