package com.newbiest.gc.rest.wltBox.print.parameter.wltLot.print.parameter;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.mms.model.MaterialLotUnit;
import com.newbiest.mms.service.MmsService;
import com.newbiest.mms.service.PrintService;
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
import java.util.Map;

@RestController
@RequestMapping("/gc")
@Slf4j
@Api(value="/gc", tags="gc客制化接口", description = "GalaxyCore客制化接口")
public class GcGetWltBoxPrintParaController extends AbstractRestController {

    @Autowired
    PrintService printService;

    @ApiOperation(value = "获取来料单箱标签参数")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "GcGetWltBoxPrintParaRequest")
    @RequestMapping(value = "/getPrintWltBoxParameter", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public GcGetWltBoxPrintParaResponse execute(@RequestBody GcGetWltBoxPrintParaRequest request) throws Exception {
        GcGetWltBoxPrintParaResponse response = new GcGetWltBoxPrintParaResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        GcGetWltBoxPrintParaResponseBody responseBody = new GcGetWltBoxPrintParaResponseBody();

        GcGetWltBoxPrintParaRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();
        List<MaterialLotUnit> materialLotUnitList = requestBody.getMaterialLotUnitList();
        if(GcGetWltBoxPrintParaRequest.ACTION_TYPE_WLT_BBOX_LABEL.equals(actionType)){
            List<Map<String, Object>> parameterMapList = printService.printWltBboxLabel(materialLotUnitList);
            responseBody.settingClientPrint(parameterMapList);
        } else if (GcGetWltBoxPrintParaRequest.ACTION_TYPE_WAFER_LABEL.equals(actionType)){
            List<Map<String, Object>> parameterMapList = printService.printWaferLabel(materialLotUnitList);
            responseBody.settingClientPrint(parameterMapList);
        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }


        response.setBody(responseBody);
        return response;
    }

}
