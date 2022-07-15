package com.newbiest.gc.rest.vbox.print.parameter;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.gc.model.MesPackedLot;
import com.newbiest.gc.service.GcService;
import com.newbiest.mms.model.LabelTemplate;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.service.MmsService;
import com.newbiest.mms.service.PackageService;
import com.newbiest.mms.service.PrintService;
import com.newbiest.msg.Request;
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

        String actionType = requestBody.getActionType();
        if(GetVboxPrintParaRequest.ACTION_QUERY.equals(actionType)){
            MesPackedLot mesPackedLot = gcService.queryVboxByTableRrnAndVboxId(requestBody.getTableRrn(), requestBody.getVboxId());
            responseBody.setMesPackedLot(mesPackedLot);
        } else if(GetVboxPrintParaRequest.ACTION_PRINT_LABLE.equals(actionType)){
            List<MesPackedLot> mesPackedLots = requestBody.getMesPackedLots();
            List<MesPackedLot> comMesPackedList = mesPackedLots.stream().filter(mesPackedLot -> MesPackedLot.PRODUCT_CATEGORY_COM.equals(mesPackedLot.getProductCategory())).collect(Collectors.toList());
            List<MesPackedLot> ftMesPackedList = mesPackedLots.stream().filter(mesPackedLot -> !MesPackedLot.PRODUCT_CATEGORY_COM.equals(mesPackedLot.getProductCategory())).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(comMesPackedList)){
                List<Map<String, Object>> parameterMapList = Lists.newArrayList();
                for (MesPackedLot mesPackedLot : comMesPackedList) {
                    Map<String, Object> parameterMap = Maps.newHashMap();
                    MesPackedLot vBox = gcService.findByPackedLotId(mesPackedLot.getBoxId());
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
                    StringBuilder QRCodeInfo = new StringBuilder();
                    QRCodeInfo.append(vBox.getBoxId() + MesPackedLot.STRING_LINE);
                    QRCodeInfo.append(vBox.getProductId().substring(0, vBox.getProductId().length()-4) + MesPackedLot.STRING_LINE);
                    QRCodeInfo.append(vBox.getBoxId().substring(4, vBox.getBoxId().length()-4) + MesPackedLot.STRING_LINE);
                    QRCodeInfo.append(vBox.getBoxId().substring(4, vBox.getBoxId().length()) + MesPackedLot.STRING_LINE);
                    QRCodeInfo.append("GC" + MesPackedLot.STRING_LINE + vBox.getQuantity());
                    parameterMap.put("QRCodeInfo", QRCodeInfo.toString());
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
                List<Map<String, Object>> mapList = printService.rePrintVBxoLabel(parameterMapList, LabelTemplate.PRINT_COM_VBOX_LABEL);
                responseBody.settingClientPrint(mapList);
            }
            if(CollectionUtils.isNotEmpty(ftMesPackedList)){
                List<Map<String, Object>> parameterMapList = Lists.newArrayList();
                for (MesPackedLot mesPackedLot : ftMesPackedList) {
                    String subCode = gcService.getEncryptionSubCode(mesPackedLot.getGrade(), mesPackedLot.getLevelTwoCode());
                    Map<String, Object> parameterMap = Maps.newHashMap();
                    parameterMap.put("BOXID", mesPackedLot.getBoxId());
                    parameterMap.put("DEVICEID", mesPackedLot.getProductId().substring(0, mesPackedLot.getProductId().lastIndexOf("-")));
                    parameterMap.put("SUBCODE", subCode);
                    parameterMap.put("NUMBER", mesPackedLot.getQuantity().toString());
                    if(MaterialLot.IMPORT_COG.equals(mesPackedLot.getProductCategory())){
                        for(int i=1; i <= 5; i++){
                            parameterMap.put("PACKED" + i, StringUtils.EMPTY);
                            parameterMap.put("PACKEDQTY" + i, StringUtils.EMPTY);
                        }
                        parameterMapList.add(parameterMap);
                    } else {
                        List<MesPackedLot> tboxList = gcService.findByParentRrn(mesPackedLot.getPackedLotRrn());
                        int number = 1;
                        if (CollectionUtils.isNotEmpty(tboxList)) {
                            if(tboxList.size() <= 5 ){
                                for (MesPackedLot tbox : tboxList) {
                                    parameterMap.put("PACKED" + number, tbox.getBoxId());
                                    parameterMap.put("PACKEDQTY" + number, tbox.getQuantity().toString());
                                    number++;
                                }
                                for (int j = number; j <= 5; j++) {
                                    parameterMap.put("PACKED" + j, StringUtils.EMPTY);
                                    parameterMap.put("PACKEDQTY" + j, StringUtils.EMPTY);
                                }
                                parameterMapList.add(parameterMap);
                            } else {
                                for(number=1; number < 5; number++){
                                    MesPackedLot packedLot = tboxList.get(number);
                                    parameterMap.put("PACKED" + number, packedLot.getBoxId());
                                    parameterMap.put("PACKEDQTY" + number, packedLot.getQuantity().toString());
                                }
                                int count = 0;
                                for(int j = 4; j< tboxList.size();j++){
                                    count += tboxList.get(j).getQuantity();
                                }
                                parameterMap.put("PACKED5", "OTHERS");
                                parameterMap.put("PACKEDQTY5", String.valueOf(count));
                                parameterMapList.add(parameterMap);
                            }
                        }
                    }
                }
                List<Map<String, Object>> mapList = printService.rePrintVBxoLabel(parameterMapList, LabelTemplate.PRINT_FT_VBOX_LABEL);
                responseBody.settingClientPrint(mapList);
            }
        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }
        response.setBody(responseBody);
        return response;
    }

}
