package com.newbiest.gc.rest.wltcp.print.parameter;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.base.utils.ThreadLocalContext;
import com.newbiest.gc.GcExceptions;
import com.newbiest.gc.model.MesPackedLot;
import com.newbiest.gc.service.GcService;
import com.newbiest.mms.service.MmsService;
import com.newbiest.mms.service.MaterialLotUnitService;
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
import com.newbiest.mms.model.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/gc")
@Slf4j
@Api(value="/gc", tags="gc客制化接口", description = "GalaxyCore客制化接口")
public class PrintWltCpLotController extends AbstractRestController {

    @Autowired
    GcService gcService;

    @Autowired
    MmsService mmsService;

    @Autowired
    MaterialLotUnitService mLotUnitService;

    @Autowired
    PackageService packageService;

    @Autowired
    PrintService printService;

    @ApiOperation(value = "获取WLT/CPlot数据")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "PrintWltCpLotRequest")
    @RequestMapping(value = "/printWltCpLot", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public PrintWltCpLotResponse execute(@RequestBody PrintWltCpLotRequest request) throws Exception {
        PrintWltCpLotResponse response = new PrintWltCpLotResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        PrintWltCpLotResponseBody responseBody = new PrintWltCpLotResponseBody();

        PrintWltCpLotRequestBody requestBody = request.getBody();
        Map<String, String> parameterMap = Maps.newHashMap();
        MaterialLot materialLot = requestBody.getMaterialLot();
        if(materialLot != null){
            parameterMap = gcService.getWltCpPrintParameter(materialLot);
//            printService.printWltOrCpLabel(materialLot, ThreadLocalContext.getSessionContext());
        }
        responseBody.setParameterMap(parameterMap);
        response.setBody(responseBody);
        return response;
    }

}
