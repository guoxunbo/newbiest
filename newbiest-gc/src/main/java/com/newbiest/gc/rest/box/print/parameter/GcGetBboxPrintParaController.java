package com.newbiest.gc.rest.box.print.parameter;

import com.google.common.collect.Maps;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.StringUtils;
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
public class GcGetBboxPrintParaController extends AbstractRestController {

    @Autowired
    GcService gcService;

    @Autowired
    MmsService mmsService;

    @Autowired
    PackageService packageService;

    @ApiOperation(value = "获取箱标签参数")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "GcGetBboxPrintParaRequest")
    @RequestMapping(value = "/getPrintBboxParameter", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public GcGetBboxPrintParaResponse execute(@RequestBody GcGetBboxPrintParaRequest request) throws Exception {
        GcGetBboxPrintParaResponse response = new GcGetBboxPrintParaResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        GcGetBboxPrintParaResponseBody responseBody = new GcGetBboxPrintParaResponseBody();

        GcGetBboxPrintParaRequestBody requestBody = request.getBody();
        Map<String, String> parameterMap = Maps.newHashMap();
        Map<String, String> customerParameterMap = Maps.newHashMap();

        MaterialLot materialLot = mmsService.getMLotByObjectRrn(requestBody.getMaterialLotRrn());
        parameterMap.put("barcode", materialLot.getMaterialLotId());
        parameterMap.put("device", materialLot.getMaterialName());
        parameterMap.put("wafernum", materialLot.getCurrentQty().toPlainString());
        if(MaterialLot.PRODUCT_CATEGORY.equals(materialLot.getReserved7())){
            parameterMap.put("subcode", materialLot.getReserved1() + materialLot.getGrade());
        } else {
            String subcode = gcService.getEncryptionSubCode(materialLot.getGrade(), materialLot.getReserved1());
            parameterMap.put("subcode", subcode);
        }

        List<MaterialLot> packageDetailLots = packageService.getPackageDetailLots(requestBody.getMaterialLotRrn());
        int i = 1;
        if (CollectionUtils.isNotEmpty(packageDetailLots)) {
            for (MaterialLot packedMLot : packageDetailLots) {
                parameterMap.put("VBox" + i, packedMLot.getMaterialLotId());
                i++;
            }
        }
        for (int j = i; j <= 10; j++) {
            parameterMap.put("VBox" + j, StringUtils.EMPTY);
        }
        if (!StringUtils.isNullOrEmpty(materialLot.getReserved18())){
            customerParameterMap.put("CSNAME",materialLot.getReserved18());
            responseBody.setCustomerParameter(customerParameterMap);
        }
        responseBody.setParameters(parameterMap);
        response.setBody(responseBody);
        return response;
    }

}
