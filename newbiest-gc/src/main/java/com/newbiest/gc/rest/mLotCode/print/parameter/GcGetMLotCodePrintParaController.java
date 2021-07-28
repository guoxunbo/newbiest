package com.newbiest.gc.rest.mLotCode.print.parameter;

import com.google.common.collect.Lists;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.gc.service.GcService;
import com.newbiest.mms.model.MaterialLot;
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
public class GcGetMLotCodePrintParaController extends AbstractRestController {

    @Autowired
    GcService gcService;

    @ApiOperation(value = "获取箱/真空包物料编码标签参数")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "GcGetMLotCodePrintParaRequest")
    @RequestMapping(value = "/getMLotCodePrintParameter", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public GcGetMLotCodePrintParaResponse execute(@RequestBody GcGetMLotCodePrintParaRequest request) throws Exception {
        GcGetMLotCodePrintParaResponse response = new GcGetMLotCodePrintParaResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        GcGetMLotCodePrintParaResponseBody responseBody = new GcGetMLotCodePrintParaResponseBody();

        GcGetMLotCodePrintParaRequestBody requestBody = request.getBody();
        List<Map<String, String>> parameterMapList = Lists.newArrayList();

        parameterMapList = gcService.getMlotCodePrintParameter(requestBody.getMaterialLot(),  requestBody.getPrintType());

        responseBody.setParameterMapList(parameterMapList);
        response.setBody(responseBody);
        return response;
    }

}
