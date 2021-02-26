package com.newbiest.mms.rest.materiallot.release;

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

@RestController
@Slf4j
@RequestMapping("/mms")
@Api(value="/mms", tags="Vanchip", description = "Vanchip客制化接口")
public class ReleaseMLotContorller {

    @Autowired
    MmsService mmsService;

    @ApiOperation("释放物料批次")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "ReleaseMLotRequest")
    @RequestMapping(value = "/releaseMaterialLot", method = RequestMethod.POST)
    public ReleaseMLotResponse excute(@RequestBody ReleaseMLotRequest request){
        ReleaseMLotRequestBody requestBody = request.getBody();
        ReleaseMLotResponse response = new ReleaseMLotResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        ReleaseMLotResponseBody responseBody = new ReleaseMLotResponseBody();

        mmsService.releaseMaterialLot(requestBody.getMaterialLotHolds(), requestBody.getMaterialLotAction());
        response.setBody(responseBody);
        return response;
    }
}
