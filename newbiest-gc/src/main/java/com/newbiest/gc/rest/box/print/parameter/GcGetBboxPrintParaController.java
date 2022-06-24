package com.newbiest.gc.rest.box.print.parameter;

import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.gc.service.GcService;
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

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/gc")
@Slf4j
@Api(value="/gc", tags="gc客制化接口", description = "GalaxyCore客制化接口")
public class GcGetBboxPrintParaController extends AbstractRestController {

    @Autowired
    PrintService printService;

    @Autowired
    MmsService mmsService;

    @Autowired
    GcService gcService;

    @ApiOperation(value = "获取箱标签参数")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "GcGetBboxPrintParaRequest")
    @RequestMapping(value = "/getPrintBboxParameter", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public GcGetBboxPrintParaResponse execute(@RequestBody GcGetBboxPrintParaRequest request) throws Exception {
        GcGetBboxPrintParaResponse response = new GcGetBboxPrintParaResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        GcGetBboxPrintParaResponseBody responseBody = new GcGetBboxPrintParaResponseBody();

        GcGetBboxPrintParaRequestBody requestBody = request.getBody();

        MaterialLot materialLot = mmsService.getMLotByObjectRrn(requestBody.getMaterialLotRrn());
        if(MaterialLot.LCD_PACKCASE.equals(materialLot.getPackageType()) || MaterialLot.HK_LCD_PACKCASE.equals(materialLot.getPackageType())){
            List<Map<String, Object>> mapList = printService.printLCDBoxLabel(materialLot, requestBody.getPrintCount());
            responseBody.settingClientPrint(mapList);
        } else {
            String subcode = materialLot.getReserved1() + materialLot.getGrade();
            if(!MaterialLot.PRODUCT_CATEGORY.equals(materialLot.getReserved7())){
                subcode = gcService.getEncryptionSubCode(materialLot.getGrade(), materialLot.getReserved1());
            }
            List<Map<String, Object>> mapList = printService.printComBoxAndCustomerLabel(materialLot, subcode, requestBody.getPrintCount());
            responseBody.settingClientPrint(mapList);
        }

        response.setBody(responseBody);
        return response;
    }

}
