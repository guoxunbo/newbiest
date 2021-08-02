package com.newbiest.vanchip.rest.rawmaterial;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.msg.Request;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.commom.sm.exception.StatusMachineExceptions;
import com.newbiest.mms.model.Material;
import com.newbiest.mms.model.RawMaterial;
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

@RestController("VCRawMaterialController")
@RequestMapping("/vc")
@Slf4j
@Api(value="/vc", tags="VC客制化", description = "物料管理相关")
public class RawMaterialController extends AbstractRestController {

    @Autowired
    VanChipService vanchipService;

    @Autowired
    MaterialStatusModelRepository materialStatusModelRepository;

    @ApiOperation(value = "对源物料做操作", notes = "导入保存")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "RawMaterialRequest")
    @RequestMapping(value = "/rawMaterialManager", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public RawMaterialResponse execute(@RequestBody RawMaterialRequest request) throws Exception {
        RawMaterialResponse response = new RawMaterialResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        RawMaterialResponseBody responseBody = new RawMaterialResponseBody();

        RawMaterialRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();
        List<RawMaterial> rawMaterials = requestBody.getDataList();

        if (RawMaterialRequest.ACTION_IMPORT_SAVE.equals(actionType)) {
            MaterialStatusModel statusModel = materialStatusModelRepository.findOneByName(Material.DEFAULT_STATUS_MODEL);
            if (statusModel == null) {
                throw new ClientException(StatusMachineExceptions.STATUS_MODEL_IS_NOT_EXIST);
            }

            for (RawMaterial rawMaterial: rawMaterials){
                rawMaterial.setStatusModelRrn(statusModel.getObjectRrn());
                vanchipService.saveRawMaterial(rawMaterial);
            }
        } else if (RawMaterialRequest.ACTION_MERGE.equals(actionType)){
            RawMaterial rawMaterial = rawMaterials.get(0);
            rawMaterial = vanchipService.saveRawMaterial(rawMaterial);
            responseBody.setMaterial(rawMaterial);
        }else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }
        response.setBody(responseBody);
        return response;
    }

}
