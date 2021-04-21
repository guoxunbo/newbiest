package com.newbiest.vanchip.rest.labmaterial;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.msg.Request;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.mms.model.LabMaterial;
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
@Api(value="/vc", tags="VC客制化", description = "物料管理相关")
public class LabMaterialController extends AbstractRestController {

    @Autowired
    VanChipService vanchipService;

    @ApiOperation(value = "对实验室物料做操作", notes = "导入保存,修改")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "LabMaterialRequest")
    @RequestMapping(value = "/labMaterialManager", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public LabMaterialResponse execute(@RequestBody LabMaterialRequest request) throws Exception {
        LabMaterialResponse response = new LabMaterialResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        LabMaterialResponseBody responseBody = new LabMaterialResponseBody();

        LabMaterialRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();
        if (LabMaterialRequest.ACTION_MERGE.equals(actionType)){
            LabMaterial material = vanchipService.saveLabMaterial(requestBody.getMaterial());
            responseBody.setMaterial(material);
        }else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }

        response.setBody(responseBody);
        return response;
    }

}
