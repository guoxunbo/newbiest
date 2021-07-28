package com.newbiest.gc.rest.vbox.print.parameter;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.gc.model.MesPackedLot;
import com.newbiest.gc.service.GcService;
import com.newbiest.mms.model.MaterialLot;
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
public class GetVboxPrintParaController extends AbstractRestController {

    @Autowired
    GcService gcService;

    @Autowired
    MmsService mmsService;

    @Autowired
    PackageService packageService;

    @Autowired
    PrintService printService;

    @ApiOperation(value = "获取VBOX标签参数")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "GetVboxPrintParaRequest")
    @RequestMapping(value = "/getPrintVboxParameter", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public GetVboxPrintParaResponse execute(@RequestBody GetVboxPrintParaRequest request) throws Exception {
        GetVboxPrintParaResponse response = new GetVboxPrintParaResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        GetVboxPrintParaResponseBody responseBody = new GetVboxPrintParaResponseBody();

        GetVboxPrintParaRequestBody requestBody = request.getBody();

        List<MesPackedLot> mesPackedLots = requestBody.getMesPackedLots();

        List<Map<String, Object>> parameterMapList = Lists.newArrayList();

        for (MesPackedLot mesPackedLot : mesPackedLots) {
            Map<String, Object> parameterMap = Maps.newHashMap();
            MesPackedLot vBox = gcService.findByPackedLotRrn(mesPackedLot.getPackedLotRrn());
            parameterMap.put("BOXID", vBox.getBoxId());
            parameterMap.put("DEVICEID", vBox.getProductId());
            parameterMap.put("GRADE", vBox.getGrade());

            if(MesPackedLot.PRODUCT_CATEGORY_FT.equals(mesPackedLot.getProductCategory())){
                String subCode = gcService.getEncryptionSubCode(vBox.getGrade(), vBox.getLevelTwoCode());
                parameterMap.put("SUBCODE", subCode);
            } else {
                parameterMap.put("SUBCODE", vBox.getLevelTwoCode() + vBox.getGrade());
            }

            parameterMap.put("NUMBER", vBox.getQuantity().toString());
            if(StringUtils.isNullOrEmpty(vBox.getProductionNote()) || StringUtils.isNullOrEmpty(vBox.getWorkorderId())){
                parameterMap.put("PRODUCTNOTE",StringUtils.EMPTY);
            } else {
                parameterMap.put("PRODUCTNOTE",vBox.getProductionNote());
            }

            if(StringUtils.isNullOrEmpty(vBox.getWorkorderId())){
                for(int i=1; i<=4; i++){
                    parameterMap.put("PACKED" + i, StringUtils.EMPTY);
                    parameterMap.put("PACKEDQTY" + i, StringUtils.EMPTY);
                }
                parameterMapList.add(parameterMap);
            } else {
                List<MesPackedLot> mesPackedLotDetails = gcService.findByParentRrn(vBox.getPackedLotRrn());
                int i = 1;
                if (CollectionUtils.isNotEmpty(mesPackedLotDetails)) {
                    if(mesPackedLotDetails.size() <= 4 ){
                        for (MesPackedLot mesPackedLotDetail : mesPackedLotDetails) {
                            parameterMap.put("PACKED" + i, mesPackedLotDetail.getBoxId());
                            parameterMap.put("PACKEDQTY" + i, mesPackedLotDetail.getQuantity().toString());
                            i++;
                        }
                        for (int j = i; j <= 4; j++) {
                            parameterMap.put("PACKED" + j, StringUtils.EMPTY);
                            parameterMap.put("PACKEDQTY" + j, StringUtils.EMPTY);
                        }
                        parameterMapList.add(parameterMap);
                    } else {
                        for(i=1; i<4; i++){
                            MesPackedLot mesPackedLotDetail = mesPackedLotDetails.get(i);
                            parameterMap.put("PACKED" + i, mesPackedLotDetail.getBoxId());
                            parameterMap.put("PACKEDQTY" + i, mesPackedLotDetail.getQuantity().toString());
                        }

                        int count = 0;
                        for(int j = 3; j< mesPackedLotDetails.size();j++){
                            count += mesPackedLotDetails.get(j).getQuantity();
                        }
                        parameterMap.put("PACKED4", "OTHERS");
                        parameterMap.put("PACKEDQTY4", String.valueOf(count));
                        parameterMapList.add(parameterMap);
                    }
                }
            }
        }
        printService.rePrintVBxoLabel(parameterMapList);

        response.setBody(responseBody);
        return response;
    }

}
