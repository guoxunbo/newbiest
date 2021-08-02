package com.newbiest.vanchip.rest.partsmaterial;

import com.google.common.collect.Lists;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.msg.Request;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.mms.model.Material;
import com.newbiest.mms.model.Parts;
import com.newbiest.mms.service.MmsService;
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
@Api(value="/vc", tags="PartsManagerSystem", description = "备件管理相关")
public class PartsMaterialController extends AbstractRestController {

    @Autowired
    VanChipService vanChipService;

    @Autowired
    MmsService mmsService;

    @ApiOperation(value = "对备件做操作", notes = "备件操作")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "PartsMaterialRequest")
    @RequestMapping(value = "/partsMaterialManage", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public PartsMaterialResponse execute(@RequestBody PartsMaterialRequest request) throws Exception {
        PartsMaterialResponse response = new PartsMaterialResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        PartsMaterialResponseBody responseBody = new PartsMaterialResponseBody();

        PartsMaterialRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();
        List<Parts> dataList = requestBody.getDataList();

        MaterialStatusModel statusModel = mmsService.getStatusModelByName(Material.DEFAULT_STATUS_MODEL, true);

        if(PartsMaterialRequest.ACTION_MERGE_PARTS.equals(actionType)){
            List<Parts> partsList = Lists.newArrayList();
            for (Parts parts : dataList) {
                if (StringUtils.isNullOrEmpty(parts.getStatusModelRrn())){
                    parts.setStatusModelRrn(statusModel.getObjectRrn());
                }
                parts = vanChipService.saveParts(parts);
                partsList.add(parts);
            }
            responseBody.setDataList(partsList);

        }else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }

        response.setBody(responseBody);
        return response;
    }

}
