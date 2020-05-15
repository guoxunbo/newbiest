package com.newbiest.gc.rest.boxQRCode.print.paamater;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.gc.rest.box.print.parameter.GcGetBboxPrintParaRequest;
import com.newbiest.gc.rest.box.print.parameter.GcGetBboxPrintParaRequestBody;
import com.newbiest.gc.rest.box.print.parameter.GcGetBboxPrintParaResponse;
import com.newbiest.gc.rest.box.print.parameter.GcGetBboxPrintParaResponseBody;
import com.newbiest.gc.service.GcService;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.service.MmsService;
import com.newbiest.mms.service.PackageService;
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
    MmsService mmsService;

    @Autowired
    PackageService packageService;

    @ApiOperation(value = "获取箱/真空包二维码标签参数")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "GcGetBoxQRCodePrintParaRequest")
    @RequestMapping(value = "/getPrintBoxQRCodeParameter", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public GcGetBoxQRCodePrintParaResponse execute(@RequestBody GcGetBoxQRCodePrintParaRequest request) throws Exception {
        GcGetBoxQRCodePrintParaResponse response = new GcGetBoxQRCodePrintParaResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        GcGetBoxQRCodePrintParaResponseBody responseBody = new GcGetBoxQRCodePrintParaResponseBody();

        GcGetBoxQRCodePrintParaRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();

        List<Map<String, String>> parameterMapList = Lists.newArrayList();
        Map<String, String> parameterMap = Maps.newHashMap();

        MaterialLot materialLot = requestBody.getMaterialLot();
        String printVboxLabelFlag = requestBody.getPrintVboxLabelFlag();
        if(GcGetBoxQRCodePrintParaRequest.ACTION_COB_PRINT_LABEL.equals(actionType)){
            parameterMap = gcService.getCOBBoxLabelPrintParamater(materialLot);
            responseBody.setParameterMap(parameterMap);
        } else if(GcGetBoxQRCodePrintParaRequest.ACTION_PRINT_QRCODE_LABEL.equals(actionType)){
            parameterMapList = gcService.getBoxQRCodeLabelPrintParamater(materialLot, printVboxLabelFlag);
            responseBody.setParameterMapList(parameterMapList);
        } else{
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }

        response.setBody(responseBody);
        return response;
    }

}
