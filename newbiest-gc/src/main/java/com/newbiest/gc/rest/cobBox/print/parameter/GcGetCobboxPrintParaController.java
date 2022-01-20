package com.newbiest.gc.rest.cobBox.print.parameter;

import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.mms.service.PrintService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/gc")
@Slf4j
@Api(value="/gc", tags="gc客制化接口", description = "GalaxyCore客制化接口")
public class GcGetCobboxPrintParaController extends AbstractRestController {

    @Autowired
    PrintService printService;

    @ApiOperation(value = "获取COB箱标签参数")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "GcGetCOBboxPrintParaRequest")
    @RequestMapping(value = "/getPrintCOBboxParameter", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public GcGetCobboxPrintParaResponse execute(@RequestBody GcGetCobboxPrintParaRequest request) throws Exception {
        GcGetCobboxPrintParaResponse response = new GcGetCobboxPrintParaResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());

        GcGetCobboxPrintParaResponseBody responseBody = new GcGetCobboxPrintParaResponseBody();
        GcGetCobboxPrintParaRequestBody requestBody = request.getBody();

        Map<String, Object> stringObjectMap = printService.printCobBoxLabel(requestBody.getMaterialLot(), requestBody.getPrintCount());
        responseBody.settingClientPrint(stringObjectMap);

        response.setBody(responseBody);
        return response;
    }

}
