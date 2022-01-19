package com.newbiest.gc.rest.boxQRCode.print.paamater;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.gc.GcExceptions;
import com.newbiest.gc.service.GcService;
import com.newbiest.mms.model.MaterialLot;
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
public class GcGetBoxQRCodePrintParaController extends AbstractRestController {

    @Autowired
    GcService gcService;

    @Autowired
    PrintService printService;

    @ApiOperation(value = "获取箱/真空包二维码标签参数")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "GcGetBoxQRCodePrintParaRequest")
    @RequestMapping(value = "/getPrintBoxQRCodeParameter", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public GcGetBoxQRCodePrintParaResponse execute(@RequestBody GcGetBoxQRCodePrintParaRequest request) throws Exception {
        GcGetBoxQRCodePrintParaResponse response = new GcGetBoxQRCodePrintParaResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        GcGetBoxQRCodePrintParaResponseBody responseBody = new GcGetBoxQRCodePrintParaResponseBody();

        GcGetBoxQRCodePrintParaRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();

        MaterialLot materialLot = requestBody.getMaterialLot();
        if(StringUtils.isNullOrEmpty(materialLot.getReserved16())){
            throw new ClientParameterException(GcExceptions.MATERIALLOT_RESERVED_ORDER_IS_NULL, materialLot.getMaterialLotId());
        }
        String printVboxLabelFlag = requestBody.getPrintVboxLabelFlag();
        if(GcGetBoxQRCodePrintParaRequest.ACTION_COB_PRINT_LABEL.equals(actionType)){

            Map<String, Object> params = printService.printCobBBoxLabel(materialLot);
            responseBody.settingClientPrint(params);

        } else if(GcGetBoxQRCodePrintParaRequest.ACTION_PRINT_QRCODE_LABEL.equals(actionType)){

            List<Map<String, Object>> mapList = printService.printBoxQRCodeLabel(materialLot, printVboxLabelFlag);
            responseBody.settingClientPrint(mapList);

        } else{
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }

        response.setBody(responseBody);
        return response;
    }

}
