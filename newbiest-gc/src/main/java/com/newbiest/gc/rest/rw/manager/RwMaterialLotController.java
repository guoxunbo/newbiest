package com.newbiest.gc.rest.rw.manager;

import com.google.common.collect.Lists;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.gc.model.MesPackedLot;
import com.newbiest.gc.service.GcService;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.MaterialLotUnit;
import com.newbiest.mms.service.MaterialLotUnitService;
import com.newbiest.mms.service.MmsService;
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

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController("RwMaterialLotController")
@RequestMapping("/gc")
@Slf4j
@Api(value="/gc", tags="gc客制化接口", description = "GalaxyCore客制化接口")
public class RwMaterialLotController {

    @Autowired
    GcService gcService;

    @Autowired
    MmsService mmsService;

    @Autowired
    MaterialLotUnitService materialLotUnitService;

    @Autowired
    PrintService printService;

    @ApiOperation(value = "RwManager", notes = "RW管理")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "RwMaterialLotRequest")
    @RequestMapping(value = "/rwMaterialLotManager", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public RwMaterialLotResponse execute(@RequestBody RwMaterialLotRequest request) throws Exception {
        RwMaterialLotResponse response = new RwMaterialLotResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());

        RwMaterialLotResponseBody responseBody = new RwMaterialLotResponseBody();
        RwMaterialLotRequestBody requestBody = request.getBody();

        String actionType = requestBody.getActionType();
        if (RwMaterialLotRequest.ACTION_QUERY_PRINT_PARAMETER.equals(actionType)) {
            List<Map<String, Object>> mapList = printService.printRwLotIssueLabel(requestBody.getMaterialLotList(), requestBody.getPrintCount());
            responseBody.settingClientPrint(mapList);

        } else if(RwMaterialLotRequest.ACTION_RECEIVE_PACKEDLOT.equals(actionType)) {
            List<MesPackedLot> mesPackedLots = gcService.receiveRWFinishPackedLot(requestBody.getMesPackedLots(), requestBody.getPrintLabel(), requestBody.getPrintCount());

            if(!StringUtils.isNullOrEmpty(requestBody.getPrintLabel())){
                mesPackedLots = mesPackedLots.stream().sorted(Comparator.comparing(MesPackedLot::getScanSeq)).collect(Collectors.toList());
                List<MaterialLot> materialLots = mesPackedLots.stream().map(mesPackedLot -> mmsService.getMLotByMLotId(mesPackedLot.getBoxId(), true)).collect(Collectors.toList());
                List<Map<String, Object>> mapList = printService.printRwLotCstLabel(materialLots, requestBody.getPrintCount());
                responseBody.settingClientPrint(mapList);
            }
        } else if(RwMaterialLotRequest.ACTION_PRINT_LOT_LABEL.equals(actionType)){
            Map<String, Object> parameterMap = printService.rePrintRwLotCstLabel(requestBody.getMaterialLot(), requestBody.getPrintCount());
            responseBody.settingClientPrint(parameterMap);

        } else if(RwMaterialLotRequest.ACTION_AUTO_PICK.equals(actionType)){
            List<MaterialLot> materialLots = gcService.rwTagginggAutoPickMLot(requestBody.getMaterialLotList(), requestBody.getPickQty());
            responseBody.setMaterialLotList(materialLots);
        } else if(RwMaterialLotRequest.ACTION_STOCK_OUT_TAG.equals(actionType)) {
            gcService.rwMaterialLotStockOutTag(requestBody.getMaterialLotList(), requestBody.getCustomerName(), requestBody.getAbbreviation(), requestBody.getRemarks());
        } else if(RwMaterialLotRequest.ACTION_ADD_SHIP_ORDERID.equals(actionType)) {
            gcService.rwMaterialLotAddShipOrderId(requestBody.getMaterialLotList(), requestBody.getShipOrderId());
        } else if(RwMaterialLotRequest.ACTION_CANCEL_SHIP_ORDERID.equals(actionType)){
            gcService.rwMaterialLotCancelShipOrderId(requestBody.getMaterialLotList());
        } else if(RwMaterialLotRequest.ACTION_PREVIEW.equals(actionType)){
            List<MaterialLot> materialLotList = gcService.previewRwShipTagUpdateMaterialLotList(requestBody.getMaterialLotList());
            responseBody.setMaterialLotList(materialLotList);
        } else if(RwMaterialLotRequest.ACTION_UN_STOCK_OUT_TAG.equals(actionType)){
            gcService.rwMaterialLotCancelStockTag(requestBody.getMaterialLotList());
        } else if(RwMaterialLotRequest.ACTION_QUERY_MLOT.equals(actionType)){
            MaterialLot materialLot = gcService.getMaterialLotByTableRrnAndMaterialLotIdOrLotId(requestBody.getTableRrn(), requestBody.getQueryLotId());
            responseBody.setMaterialLot(materialLot);
        } else if(RwMaterialLotRequest.ACTION_STOCK_OUT.equals(actionType)){
            gcService.rwStockOut(requestBody.getMaterialLotList(), requestBody.getDocumentLineList());
        } else if(RwMaterialLotRequest.ACTION_GET_RW_PRINT_PARAMETER.equals(actionType)){
            MaterialLot materialLot = mmsService.getMLotByObjectRrn(requestBody.getMaterialLotRrn());

            Map<String, Object> parameterMap = printService.printRwCstLabel(materialLot, requestBody.getPrintCount());
            responseBody.settingClientPrint(parameterMap);

        }else if(RwMaterialLotRequest.ACTION_GET_RW_STOCK_OUT.equals(actionType)){
            MaterialLot materialLot = mmsService.getMLotByObjectRrn(requestBody.getMaterialLotRrn());
            Map<String, Object> parameterMap = printService.printRwStockOutLabel(materialLot);

            responseBody.settingClientPrint(parameterMap);

        } else if (RwMaterialLotRequest.ACTION_GET_RW_LABEL.equals(actionType)) {
            MaterialLot materialLot = mmsService.getMLotByObjectRrn(requestBody.getMaterialLotRrn());
            Map<String, Object> parameterMap = printService.printRWBoxLabel(materialLot);
            responseBody.settingClientPrint(parameterMap);
        } else if(RwMaterialLotRequest.ACTION_COB_QUERY_MLOTUNIT.equals(actionType)){
            List<MaterialLotUnit> materialLotUnitList = Lists.newArrayList();
            List<MaterialLot> materialLots = gcService.getMaterialLotByTableRrnAndWhereClause(requestBody.getTableRrn(), requestBody.getWhereClause());
            if(CollectionUtils.isNotEmpty(materialLots)){
                for(MaterialLot materialLot : materialLots){
                    List<MaterialLotUnit> materialLotUnits = materialLotUnitService.getUnitsByMaterialLotId(materialLot.getMaterialLotId());
                    materialLotUnitList.addAll(materialLotUnits);
                }
            }
            responseBody.setMaterialLotList(materialLots);
            responseBody.setMaterialLotUnitList(materialLotUnitList);
        } else if(RwMaterialLotRequest.ACTION_WAFER_AUTO_PICK.equals(actionType)){
            List<MaterialLotUnit> materialLotUnitList = gcService.rwTagginggAutoPickMLotUnit(requestBody.getMaterialLotUnitList(), requestBody.getPickQty());
            responseBody.setMaterialLotUnitList(materialLotUnitList);
        } else if(RwMaterialLotRequest.ACTION_WAFER_STOCK_OUT_TAG.equals(actionType)){
            gcService.cobMaterialLotUnitStockOutTag(requestBody.getMaterialLotUnitList(), requestBody.getCustomerName(), requestBody.getAbbreviation(), requestBody.getRemarks());
        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }
        response.setBody(responseBody);
        return response;
    }
}
