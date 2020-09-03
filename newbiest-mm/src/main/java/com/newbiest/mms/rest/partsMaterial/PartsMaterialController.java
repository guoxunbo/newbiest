package com.newbiest.mms.rest.partsMaterial;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.mms.exception.MmsException;
import com.newbiest.mms.model.Parts;
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
 * Created by guoZhang Luo on 2019/9/3.
 */
@RestController
@RequestMapping("/mms")
@Slf4j
@Api(value="/mms", tags="PartsManagerSystem", description = "备件管理相关")
public class PartsMaterialController extends AbstractRestController {

    @Autowired
    MmsService mmsService;

    @ApiOperation(value = "对备件做操作", notes = "备份备件操作")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "PartsMaterialRequest")
    @RequestMapping(value = "/partsMaterialManage", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public PartsMaterialResponse execute(@RequestBody PartsMaterialRequest request) throws Exception {
        PartsMaterialResponse response = new PartsMaterialResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        PartsMaterialResponseBody responseBody = new PartsMaterialResponseBody();

        PartsMaterialRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();
        Parts parts = requestBody.getParts();

        if(PartsMaterialRequest.ACTION_CREATE_PARTS.equals(actionType)){
            Parts oldParts = mmsService.getPartsByName(parts.getName());
            if(oldParts != null){
                throw new ClientParameterException(MmsException.MM_SPARE_ID_IS_EXIST, parts.getName());
            }
            parts = mmsService.saveParts(parts);
        } else if(PartsMaterialRequest.ACTION_UPDATE_PARTS.equals(actionType)){
            validateEntity(parts);
            parts = mmsService.saveParts(parts);
        }else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }
        responseBody.setParts(parts);
        response.setBody(responseBody);
        return response;
    }

}
