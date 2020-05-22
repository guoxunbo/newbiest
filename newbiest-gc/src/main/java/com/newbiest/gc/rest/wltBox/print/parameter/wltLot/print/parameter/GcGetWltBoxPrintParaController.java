package com.newbiest.gc.rest.wltBox.print.parameter.wltLot.print.parameter;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.MaterialLotUnit;
import com.newbiest.mms.service.MmsService;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/gc")
@Slf4j
@Api(value="/gc", tags="gc客制化接口", description = "GalaxyCore客制化接口")
public class GcGetWltBoxPrintParaController extends AbstractRestController {

    @Autowired
    MmsService mmsService;

    @ApiOperation(value = "获取来料单箱标签参数")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "GcGetWltBoxPrintParaRequest")
    @RequestMapping(value = "/getPrintWltBoxParameter", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public GcGetWltBoxPrintParaResponse execute(@RequestBody GcGetWltBoxPrintParaRequest request) throws Exception {
        GcGetWltBoxPrintParaResponse response = new GcGetWltBoxPrintParaResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        GcGetWltBoxPrintParaResponseBody responseBody = new GcGetWltBoxPrintParaResponseBody();

        GcGetWltBoxPrintParaRequestBody requestBody = request.getBody();
        List<Map<String, String>> parameterMapList = Lists.newArrayList();
        List<MaterialLotUnit> materialLotUnitList = requestBody.getMaterialLotUnitList();
        if(CollectionUtils.isNotEmpty(materialLotUnitList)){
            Map<String, List<MaterialLotUnit>> materialLotUnitMap = materialLotUnitList.stream().collect(Collectors.groupingBy(MaterialLotUnit:: getMaterialLotId));
            for(String materialLotId : materialLotUnitMap.keySet()){
                Map<String, String> parameterMap = Maps.newHashMap();
                MaterialLot materialLot = mmsService.getMLotByMLotId(materialLotId, true);
                parameterMap.put("LOTID", materialLot.getLotId());
                parameterMap.put("PRODUCTID", materialLot.getMaterialName());
                parameterMap.put("SECONDCODE", materialLot.getReserved1());
                parameterMap.put("LOCATION", materialLot.getReserved6());
                parameterMap.put("VENDER", materialLot.getReserved22());
                List<MaterialLotUnit> materialLotUnits = materialLotUnitMap.get(materialLotId);
                Integer waferNumber = 0;
                String unitIdList1 = "";
                String unitIdLisr2 = "";
                if(CollectionUtils.isNotEmpty(materialLotUnits)){
                    waferNumber = materialLotUnits.size();
                    for(int j = 0; j <  materialLotUnits.size() ; j++){
                        String[] unitIdList = materialLotUnits.get(j).getUnitId().split(StringUtils.SPLIT_CODE);
                        String waferSeq = unitIdList[1] + ",";
                        if(j < 12){
                            unitIdList1 = unitIdList1 + waferSeq;
                        } else {
                            unitIdLisr2 = unitIdLisr2 + waferSeq;
                        }
                    }
                }
                if(!StringUtils.isNullOrEmpty(unitIdList1)){
                    parameterMap.put("WAFERLIST1", unitIdList1);
                } else {
                    parameterMap.put("WAFERLIST1", StringUtils.EMPTY);
                }
                if(!StringUtils.isNullOrEmpty(unitIdLisr2)){
                    parameterMap.put("WAFERLIST2", unitIdLisr2);
                } else {
                    parameterMap.put("WAFERLIST2", StringUtils.EMPTY);
                }
                if(MaterialLot.IMPORT_SENSOR_CP.equals(materialLot.getReserved49())){
                    parameterMap.put("GRADE", materialLot.getGrade() + "(" + waferNumber.toString() + ")");
                    parameterMap.put("QTY", materialLot.getCurrentQty().toString());
                }  else {
                    parameterMap.put("GRADE", materialLot.getGrade());
                    parameterMap.put("QTY", waferNumber.toString());
                }
                parameterMapList.add(parameterMap);
            }
        }
        responseBody.setParameterMapList(parameterMapList);
        response.setBody(responseBody);
        return response;
    }

}
