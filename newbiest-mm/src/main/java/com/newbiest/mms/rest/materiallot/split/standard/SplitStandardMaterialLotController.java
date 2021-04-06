package com.newbiest.mms.rest.materiallot.split.standard;

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
public class SplitStandardMaterialLotController extends AbstractRestController {

    @Autowired
    MmsService mmsService;

    @ApiOperation(value = "物料批次按数量分批")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "SplitStandardMaterialLotRequest")
    @RequestMapping(value = "/splitStandardMaterialLot", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public SplitStandardMaterialLotResponse execute(@RequestBody SplitStandardMaterialLotRequest request) throws Exception {
        SplitStandardMaterialLotResponse response = new SplitStandardMaterialLotResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        SplitStandardMaterialLotResponseBody responseBody = new SplitStandardMaterialLotResponseBody();
        SplitStandardMaterialLotRequestBody requestBody = request.getBody();

        List<MaterialLot> subMaterialLots = mmsService.splitStandardMLot(requestBody.getMaterialLotAction().getMaterialLotId(), requestBody.getMaterialLotAction().getTransQty());
        responseBody.setSubMaterialLots(subMaterialLots);
        response.setBody(responseBody);
        return response;
    }

}
