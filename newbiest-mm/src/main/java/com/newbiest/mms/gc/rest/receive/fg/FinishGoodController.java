package com.newbiest.mms.gc.rest.receive.fg;

import com.newbiest.mms.gc.model.service.GcService;
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
 * Created by guoxunbo on 2019-08-21 13:15
 */
@RestController
@RequestMapping("/gc")
@Slf4j
@Api(value="/gc", tags="gc客制化接口", description = "GalaxyCore客制化接口")
public class FinishGoodController {

    @Autowired
    GcService gcService;

    @ApiOperation(value = "对完成品做操作", notes = "接收")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "FinishGoodRequest")
    @RequestMapping(value = "/finishGoodManage", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public FinishGoodResponse execute(@RequestBody FinishGoodRequest request) throws Exception {
        FinishGoodResponse response = new FinishGoodResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());

        FinishGoodResponseBody responseBody = new FinishGoodResponseBody();
        FinishGoodRequestBody requestBody = request.getBody();
        gcService.receiveFinishGood(requestBody.getMesPackedLots(), requestBody.getWarehouseRrn());

        response.setBody(responseBody);
        return response;
    }
}
