package com.newbiest.mms.rest.rawmaterial;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.base.rest.entity.EntityRequest;
import com.newbiest.mms.exception.MmsException;
import com.newbiest.mms.model.Material;
import com.newbiest.mms.model.Parts;
import com.newbiest.mms.model.RawMaterial;
import com.newbiest.mms.service.MmsService;
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

/**
 * Created by guoxunbo on 2018/7/12.
 */
@RestController
@RequestMapping("/mms")
@Slf4j
@Api(value="/mms", tags="MaterialManagerSystem", description = "物料管理相关")
public class RawMaterialController extends AbstractRestController {

    @Autowired
    MmsService mmsService;

    @ApiOperation(value = "对源物料做操作", notes = "源物料当前不考虑版本信息，故添加的时候直接active物料")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "RawMaterialRequest")
    @RequestMapping(value = "/rawMaterialManage", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public RawMaterialResponse execute(@RequestBody RawMaterialRequest request) throws Exception {
        RawMaterialResponse response = new RawMaterialResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        RawMaterialResponseBody responseBody = new RawMaterialResponseBody();

        RawMaterialRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();
        RawMaterial material = requestBody.getMaterial();
        Parts parts = requestBody.getParts();

        if (RawMaterialRequest.ACTION_CREATE.equals(actionType)) {
            //验证下名称是否存在，存在则抛异常
            Material oldData = mmsService.getRawMaterialByName(material.getName());
            if (oldData != null) {
                throw new ClientParameterException(MmsException.MM_RAW_MATERIAL_IS_EXIST, material.getName());
            }
            material = mmsService.saveRawMaterial(material);
        } else if (EntityRequest.ACTION_UPDATE.equals(actionType)) {
            validateEntity(material);
            material = mmsService.saveRawMaterial(material);
        } else if(RawMaterialRequest.ACTION_CREATE_PARTS.equals(actionType)){
            Material oldData = mmsService.getPartsByName(parts.getName());
            if(oldData != null){
                throw new ClientParameterException(MmsException.MM_SPARE_ID_IS_EXIST, material.getName());
            }
            parts = mmsService.saveParts(parts);
        } else if(RawMaterialRequest.ACTION_UPDATE_PARTS.equals(actionType)){
            validateEntity(parts);
            parts = mmsService.saveParts(parts);
        }else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }
        responseBody.setMaterial(material);
        responseBody.setParts(parts);
        response.setBody(responseBody);
        return response;
    }

}
