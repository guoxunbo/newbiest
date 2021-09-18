package com.newbiest.gc.rest.waferUnpack;

import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.gc.model.GCWorkorderRelation;
import com.newbiest.gc.service.GcService;
import com.newbiest.mms.model.MaterialLotUnit;
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
 * Created by Youqing Huang 20210915
 */
@RestController
@RequestMapping("/gc")
@Slf4j
@Api(value="/gc", tags="gc客制化接口", description = "wafer拆箱")
public class WaferUnpackController extends AbstractRestController {

    @Autowired
    GcService gcService;

    @ApiOperation(value = "waferUnpack", notes = "wafer拆箱")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "WaferUnpackRequest")
    @RequestMapping(value = "/waferUnpack", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public WaferUnpackResponse execute(@RequestBody WaferUnpackRequest request) throws Exception {
        WaferUnpackResponse response = new WaferUnpackResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        WaferUnpackResponseBody responseBody = new WaferUnpackResponseBody();
        WaferUnpackRequestBody requestBody = request.getBody();

        String actionType = requestBody.getActionType();
        List<MaterialLotUnit> materialLotUnits = requestBody.getMaterialLotUnits();
        if (WaferUnpackRequest.ACTION_WAFER_UNPACK.equals(actionType)){
            gcService.waferUnpackMLot(materialLotUnits);
        }

        response.setBody(responseBody);
        return response;
    }

}
