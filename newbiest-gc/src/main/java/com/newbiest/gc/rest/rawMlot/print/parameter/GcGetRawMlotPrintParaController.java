package com.newbiest.gc.rest.rawMlot.print.parameter;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.gc.GcExceptions;
import com.newbiest.gc.service.GcService;
import com.newbiest.mms.model.Material;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.service.MmsService;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/gc")
@Slf4j
@Api(value="/gc", tags="gc客制化接口", description = "GalaxyCore客制化接口")
public class GcGetRawMlotPrintParaController extends AbstractRestController {

    @Autowired
    PrintService printService;

    @Autowired
    MmsService mmsService;

    @Autowired
    GcService gcService;

    @ApiOperation(value = "获取原材料标签参数")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "GcGetRawMlotPrintParaRequest")
    @RequestMapping(value = "/rawMlotPrintManager", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public GcGetRawMlotPrintParaResponse execute(@RequestBody GcGetRawMlotPrintParaRequest request) throws Exception {
        GcGetRawMlotPrintParaResponse response = new GcGetRawMlotPrintParaResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        GcGetRawMlotPrintParaResponseBody responseBody = new GcGetRawMlotPrintParaResponseBody();

        GcGetRawMlotPrintParaRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();

        List<MaterialLot> materialLots = requestBody.getMaterialLots();
        Set materialTypeInfo = materialLots.stream().map(materialLot -> materialLot.getMaterialType()).collect(Collectors.toSet());
        if (materialTypeInfo != null && materialTypeInfo.size() > 1) {
            throw new ClientParameterException(GcExceptions.MATERIAL_TYPE_IS_NOT_SAME);
        }
        if (GcGetRawMlotPrintParaRequest.ACTION_RAWPRINT.equals(actionType)) {
            if (Material.MATERIAL_TYPE_IRA.equals(materialLots.get(0).getMaterialType())) {
                printService.printRawMlotIRLabel(materialLots);
            } else if (Material.MATERIAL_TYPE_GLUE.equals(materialLots.get(0).getMaterialType())) {
                printService.printRawMlotGlueLabel(materialLots);
            }
        } else if (GcGetRawMlotPrintParaRequest.ACTION_IRABOXPRINT.equals(actionType)) {
            List<MaterialLot> unPackedMLots = materialLots.stream().filter(materialLot -> StringUtils.isNullOrEmpty(materialLot.getLotId())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(unPackedMLots)){
                throw new ClientException(GcExceptions.MATERIAL_TYPE_IS_NOT_SAME);
            }
            printService.printIRABoxLabel(materialLots);
        }

        response.setBody(responseBody);
        return response;
    }

}
