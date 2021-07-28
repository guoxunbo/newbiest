package com.newbiest.gc.rest.vboxHoldSet;

import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.gc.model.GCWorkorderRelation;
import com.newbiest.gc.service.GcService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by guozhangLuo
 */
@RestController
@RequestMapping("/gc")
@Slf4j
@Api(value="/gc", tags="gc客制化接口", description = "产品二级代码设定")
public class VboxHoldSetController extends AbstractRestController {

    @Autowired
    GcService gcService;

    @ApiOperation(value = "vboxHoldSet", notes = "真空包HOLD设置")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "VboxHoldSetRequest")
    @RequestMapping(value = "/vboxHoldSet", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public VboxHoldSetResponse execute(@RequestBody VboxHoldSetRequest request) throws Exception {
        VboxHoldSetResponse response = new VboxHoldSetResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        VboxHoldSetResponseBody responseBody = new VboxHoldSetResponseBody();
        VboxHoldSetRequestBody requestBody = request.getBody();

        String actionType = requestBody.getActionType();
        GCWorkorderRelation workorderRelation = requestBody.getWorkorderRelation();
        workorderRelation = gcService.saveWorkorderGradeHoldInfo(workorderRelation, actionType);

        responseBody.setWorkorderRelation(workorderRelation);
        response.setBody(responseBody);
        return response;
    }

}
