package com.newbiest.mms.rest.materiallot.hold;

import com.newbiest.mms.dto.MaterialLotAction;
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
@Slf4j
@RequestMapping("/mms")
@Api(value="/mms", tags="Vanchip", description = "Vanchip客制化接口")
public class HoldMLotContorller {

    @Autowired
    MmsService mmsService;

    @ApiOperation("暂停物料批次")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "HoldMLotRequest")
    @RequestMapping(value = "/holdMaterialLot", method = RequestMethod.POST)
    public HoldMLotResponse excute(@RequestBody HoldMLotRequest request){
        HoldMLotRequestBody requestBody = request.getBody();
        HoldMLotResponse response = new HoldMLotResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        HoldMLotResponseBody responseBody = new HoldMLotResponseBody();

        List<MaterialLotAction> materialLotActions = requestBody.getMaterialLotActions();
        mmsService.holdMaterialLot(materialLotActions);
        response.setBody(responseBody);
        return response;
    }
}
