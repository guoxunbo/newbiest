package com.newbiest.mms.rest.materiallot.split;

import com.google.common.collect.Lists;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.msg.Request;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.service.MmsService;
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
@RequestMapping("/mms")
@Slf4j
@Api(value="/mms", tags="MaterialManagerSystem", description = "物料管理相关")
public class SplitMaterialLotController extends AbstractRestController {

    @Autowired
    MmsService mmsService;

    @ApiOperation(value = "物料批次分批")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "SplitMaterialLotRequest")
    @RequestMapping(value = "/splitMateraiLot", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public SplitMaterialLotResponse execute(@RequestBody SplitMaterialLotRequest request) throws Exception {
        SplitMaterialLotResponse response = new SplitMaterialLotResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        SplitMaterialLotResponseBody responseBody = new SplitMaterialLotResponseBody();
        SplitMaterialLotRequestBody requestBody = request.getBody();
        MaterialLot subMaterialLot = mmsService.splitMLot(requestBody.getMaterialLotAction().getMaterialLotId(), requestBody.getMaterialLotAction());
        responseBody.setMaterialLot(subMaterialLot);
        response.setBody(responseBody);
        return response;
    }

}
