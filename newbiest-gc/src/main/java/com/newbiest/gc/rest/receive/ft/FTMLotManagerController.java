package com.newbiest.gc.rest.receive.ft;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.gc.rest.receive.wafer.WaferManagerRequest;
import com.newbiest.gc.rest.receive.wafer.WaferManagerResponseBody;
import com.newbiest.gc.service.GcService;
import com.newbiest.mms.model.MaterialLotUnit;
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

import java.util.List;

/**
 * Created by guozhangLuo on 2020-10-12
 */
@RestController
@RequestMapping("/gc")
@Slf4j
@Api(value="/gc", tags="gc客制化接口", description = "GalaxyCore客制化接口")
public class FTMLotManagerController extends AbstractRestController {

    @Autowired
    GcService gcService;

    @Autowired
    MmsService mmsService;

    @ApiOperation(value = "FT来料接收、发料", notes = "接收")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "FTMLotManagerRequest")
    @RequestMapping(value = "/ftMaterialLotManager", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public FTMLotManagerResponse execute(@RequestBody FTMLotManagerRequest request) throws Exception {
        FTMLotManagerResponse response = new FTMLotManagerResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        FTMLotManagerResponseBody responseBody = new FTMLotManagerResponseBody();
        FTMLotManagerRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();

        if (WaferManagerRequest.ACTION_TYPE_RECEIVE.equals(actionType)) {
            List<MaterialLotUnit> materialLotUnits = requestBody.getMaterialLotUnitList();
            gcService.receiveFTWafer(materialLotUnits);
        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + request.getBody().getActionType());
        }
        response.setBody(responseBody);
        return response;
    }
}
