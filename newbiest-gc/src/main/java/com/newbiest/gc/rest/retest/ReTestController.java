package com.newbiest.gc.rest.retest;

import com.newbiest.gc.service.GcService;
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
public class ReTestController {

    @Autowired
    GcService gcService;

    @ApiOperation(value = "ReTest", notes = "重测发料")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "ReTestRequest")
    @RequestMapping(value = "/reTest", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public ReTestResponse execute(@RequestBody ReTestRequest request) throws Exception {
        ReTestResponse response = new ReTestResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());

        RetestResponseBody responseBody = new RetestResponseBody();
        ReTestRequestBody requestBody = request.getBody();

        gcService.reTest(requestBody.getDocumentLine(), requestBody.getMaterialLotActions());

        response.setBody(responseBody);
        return response;
    }
}
