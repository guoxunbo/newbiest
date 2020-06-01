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
    GcService gcService;

    @Autowired
    MmsService mmsService;

    @Autowired
    PackageService packageService;

    @Autowired
    MaterialLotUnitService materialLotUnitService;

    @ApiOperation(value = "获取Wlt箱标签参数")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "GcGetWltBboxPrintParaRequest")
    @RequestMapping(value = "/getPrintWltBboxParameter", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public GcGetWltBboxPrintParaResponse execute(@RequestBody GcGetWltBboxPrintParaRequest request) throws Exception {
        GcGetWltBboxPrintParaResponse response = new GcGetWltBboxPrintParaResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        GcGetWltBboxPrintParaResponseBody responseBody = new GcGetWltBboxPrintParaResponseBody();

        GcGetWltBboxPrintParaRequestBody requestBody = request.getBody();
        Map<String, String> parameterMap = Maps.newHashMap();

        MaterialLot materialLot = mmsService.getMLotByObjectRrn(requestBody.getMaterialLotRrn());
        parameterMap.put("BOXID", materialLot.getMaterialLotId());
        parameterMap.put("PRODUCTID", materialLot.getMaterialName());
        parameterMap.put("GRADE", materialLot.getGrade());
        parameterMap.put("SECONDCODE", materialLot.getReserved1());
        parameterMap.put("LOCATION", materialLot.getReserved6());
        parameterMap.put("QUANTITY", materialLot.getCurrentQty().toPlainString());
        parameterMap.put("NUMBER", materialLot.getCurrentSubQty().toPlainString());


        List<MaterialLot> packageDetailLots = packageService.getPackageDetailLots(requestBody.getMaterialLotRrn());
        int i = 1;
        if (CollectionUtils.isNotEmpty(packageDetailLots)) {
            for (MaterialLot packedMLot : packageDetailLots) {
                parameterMap.put("CSTID" + i, packedMLot.getLotId());
                parameterMap.put("WAFERQTY" + i, packedMLot.getCurrentSubQty().toString());
                List<MaterialLotUnit> materialLotUnitList = materialLotUnitService.getUnitsByMaterialLotId(packedMLot.getMaterialLotId());
                String unitIdList1 = "";
                String unitIdList2 = "";
                for(int j = 0; j <  materialLotUnitList.size() ; j++){
                    String[] unitIdList = materialLotUnitList.get(j).getUnitId().split(StringUtils.SPLIT_CODE);
                    String waferSeq = unitIdList[1] + ",";
                    if(j < 5){
                        unitIdList1 = unitIdList1 + waferSeq;
                    } else {
                        unitIdList2 = unitIdList2 + waferSeq;
                    }
                }
                if(!StringUtils.isNullOrEmpty(unitIdList1)){
                    parameterMap.put("UNITID" + i + 1 , unitIdList1);
                } else {
                    parameterMap.put("UNITID" + i + 1 , StringUtils.EMPTY);
                }
                if(!StringUtils.isNullOrEmpty(unitIdList2)){
                    parameterMap.put("UNITID" + i + 2 , unitIdList2);
                }else {
                    parameterMap.put("UNITID" + i + 2 , StringUtils.EMPTY);
                }
                i++;
            }
        }

        if(i <= 2){
            parameterMap.put("CSTID" + i, StringUtils.EMPTY);
            parameterMap.put("WAFERQTY" + i, StringUtils.EMPTY);
            parameterMap.put("UNITID" + i + 1, StringUtils.EMPTY);
            parameterMap.put("UNITID" + i + 2, StringUtils.EMPTY);
        }

        responseBody.setParameters(parameterMap);
        response.setBody(responseBody);
        return response;
    }

}
