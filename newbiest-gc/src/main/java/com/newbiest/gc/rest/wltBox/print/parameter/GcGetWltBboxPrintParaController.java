package com.newbiest.gc.rest.wltBox.print.parameter;

import com.google.common.collect.Maps;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.gc.service.GcService;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.MaterialLotUnit;
import com.newbiest.mms.service.MaterialLotUnitService;
import com.newbiest.mms.service.MmsService;
import com.newbiest.mms.service.PackageService;
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
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/gc")
@Slf4j
@Api(value="/gc", tags="gc客制化接口", description = "GalaxyCore客制化接口")
public class GcGetWltBboxPrintParaController extends AbstractRestController {

    @Autowired
    PrintService printService;

    @Autowired
    MmsService mmsService;

    @ApiOperation(value = "获取Wlt箱标签参数")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "GcGetWltBboxPrintParaRequest")
    @RequestMapping(value = "/getPrintWltBboxParameter", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public GcGetWltBboxPrintParaResponse execute(@RequestBody GcGetWltBboxPrintParaRequest request) throws Exception {
        GcGetWltBboxPrintParaResponse response = new GcGetWltBboxPrintParaResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        GcGetWltBboxPrintParaResponseBody responseBody = new GcGetWltBboxPrintParaResponseBody();
        GcGetWltBboxPrintParaRequestBody requestBody = request.getBody();

        MaterialLot materialLot = mmsService.getMLotByObjectRrn(requestBody.getMaterialLotRrn());
        printService.printWltBoxLabel(materialLot);

        response.setBody(responseBody);
        return response;
    }

}
