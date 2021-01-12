package com.newbiest.gc.rest.cobBox.print.parameter;

import com.google.common.collect.Maps;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.gc.service.GcService;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.MaterialLotUnit;
import com.newbiest.mms.service.MaterialLotUnitService;
import com.newbiest.mms.service.MmsService;
import com.newbiest.mms.service.PackageService;
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
public class GcGetCobboxPrintParaController extends AbstractRestController {

    @Autowired
    GcService gcService;

    @Autowired
    MmsService mmsService;

    @Autowired
    PackageService packageService;

    @Autowired
    MaterialLotUnitService materialLotUnitService;

    @ApiOperation(value = "获取COB箱标签参数")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "GcGetCOBboxPrintParaRequest")
    @RequestMapping(value = "/getPrintCOBboxParameter", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public GcGetCobboxPrintParaResponse execute(@RequestBody GcGetCobboxPrintParaRequest request) throws Exception {
        GcGetCobboxPrintParaResponse response = new GcGetCobboxPrintParaResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());

        GcGetCobboxPrintParaResponseBody responseBody = new GcGetCobboxPrintParaResponseBody();
        GcGetCobboxPrintParaRequestBody requestBody = request.getBody();

        Map<String, String> parameterMap = Maps.newHashMap();

        parameterMap = gcService.getCOBLabelPrintParamater(requestBody.getMaterialLot().getMaterialLotId());

        responseBody.setParameters(parameterMap);
        response.setBody(responseBody);
        return response;
    }

}