package com.newbiest.gc.rest.wltcp.print.parameter;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.gc.GcExceptions;
import com.newbiest.gc.model.MesPackedLot;
import com.newbiest.gc.service.GcService;
import com.newbiest.mms.service.MmsService;
import com.newbiest.mms.service.MaterialLotUnitService;
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
            parameterMap.put("LOTID", materialLot.getLotId());
            parameterMap.put("DEVICEID", materialLot.getMaterialName());
            parameterMap.put("QTY", materialLot.getCurrentQty().toString());
            parameterMap.put("WAFERGRADE", materialLot.getGrade());
            parameterMap.put("LOCATION", materialLot.getReserved6());
            parameterMap.put("SUBCODE", materialLot.getReserved1());
            List<MaterialLotUnit> materialLotUnitList = mLotUnitService.getUnitsByMaterialLotId(materialLot.getMaterialLotId());

            if(CollectionUtils.isNotEmpty(materialLotUnitList)){
                Integer waferQty = materialLotUnitList.size();
                parameterMap.put("WAFERQTY", waferQty.toString());
                String waferIdList1 = "";
                String waferIdList2 = "";

                for(int j = 0; j <  materialLotUnitList.size() ; j++){
                    String[] waferIdList = materialLotUnitList.get(j).getUnitId().split(StringUtils.SPLIT_CODE);
                    String waferSeq = waferIdList[1] + ",";
                    if(j < 8){
                        waferIdList1 = waferIdList1 + waferSeq;
                    } else {
                        waferIdList2 = waferIdList2 + waferSeq;
                    }
                }
                if(!StringUtils.isNullOrEmpty(waferIdList1)){
                    parameterMap.put("WAFERID1", waferIdList1);
                } else {
                    parameterMap.put("WAFERID1", StringUtils.EMPTY);
                }
                if(!StringUtils.isNullOrEmpty(waferIdList2)){
                    parameterMap.put("WAFERID2", waferIdList2);
                } else {
                    parameterMap.put("WAFERID2", StringUtils.EMPTY);
                }
            }

        }
        responseBody.setParameterMap(parameterMap);
        response.setBody(responseBody);
        return response;
    }

}
