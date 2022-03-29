package com.newbiest.gc.rest.reserved;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.base.ui.model.NBReferenceTable;
import com.newbiest.base.ui.model.NBTable;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.gc.model.GCProductNumberRelation;
import com.newbiest.gc.service.GcService;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.Material;
import com.newbiest.mms.model.MaterialLot;
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
import java.util.stream.Collectors;

/**
 * Created by guoxunbo on 2019-08-21 13:15
 */
@RestController
@RequestMapping("/gc")
@Slf4j
@Api(value="/gc", tags="gc客制化接口", description = "GalaxyCore客制化接口")
public class ReservedController extends AbstractRestController {

    @Autowired
    GcService gcService;

    @ApiOperation(value = "Reserved", notes = "备货")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "ReservedRequest")
    @RequestMapping(value = "/reserved", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public ReservedResponse execute(@RequestBody ReservedRequest request) throws Exception {
        ReservedResponse response = new ReservedResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());

        ReservedResponseBody responseBody = new ReservedResponseBody();
        ReServedRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();
        if (ReServedRequestBody.ACTION_TYPE_GET_MATERIAL_LOT.equals(actionType)) {
            List<MaterialLot> waitForReservedMaterialLots = gcService.getWaitForReservedMaterialLot(requestBody.getDocLineRrn(), requestBody.getTableRrn(), requestBody.getStockLocation(), MaterialLot.MLOT_RESERVED_DOC_VALIDATE_RULE_ID);
            responseBody.setMaterialLotList(waitForReservedMaterialLots);
        } else if(ReServedRequestBody.ACTION_TYPE_GET_OTHER_SHIP_MATERIAL_LOT.equals(actionType)){
            List<MaterialLot> otherShipReservedMLotList = gcService.getWaitForReservedMaterialLot(requestBody.getDocLineRrn(), requestBody.getTableRrn(), requestBody.getStockLocation(), MaterialLot.OTHER_SHIP_RESERVED_DOC_VALIDATE_RULE_ID);
            responseBody.setMaterialLotList(otherShipReservedMLotList);
        } else if (ReServedRequestBody.ACTION_TYPE_GET_MATERIAL_LOT_AND_USER.equals(actionType)) {
            NBTable nbTable = uiService.getDeepNBTable(requestBody.getTableRrn());
            List<MaterialLot> waitForUnReservedMaterialLots = gcService.getMaterialLotAndDocUserToUnReserved(requestBody.getTableRrn(),requestBody.getWhereClause());
            responseBody.setMaterialLotList(waitForUnReservedMaterialLots);
            responseBody.setTable(nbTable);
        } else if (ReServedRequestBody.ACTION_TYPE_RESERVED.equals(actionType)) {
            gcService.reservedMaterialLot(requestBody.getDocLineRrn(), requestBody.getMaterialLotActions(), requestBody.getStockNote(), MaterialLot.MLOT_RESERVED_DOC_VALIDATE_RULE_ID);
        }else if(ReServedRequestBody.ACTION_TYPE_OTHER_SHIP_RESERVED.equals(actionType)){
            gcService.reservedMaterialLot(requestBody.getDocLineRrn(), requestBody.getMaterialLotActions(), requestBody.getStockNote(), MaterialLot.OTHER_SHIP_RESERVED_DOC_VALIDATE_RULE_ID);
        } else if (ReServedRequestBody.ACTION_TYPE_UN_RESERVED.equals(actionType)) {
            gcService.unReservedMaterialLot(requestBody.getMaterialLotActions());
        } else if (ReServedRequestBody.ACTION_GET_PACKED_MLOTS.equals(actionType)) {
            List<MaterialLotAction> packedLotList = requestBody.getMaterialLotActions();
            List<String> packedLotIdList  = packedLotList.stream().map(MaterialLotAction :: getMaterialLotId).collect(Collectors.toList());
            List<MaterialLot> materialLots = gcService.getPackedDetailsAndNotReserved(packedLotIdList);
            responseBody.setMaterialLotList(materialLots);
        } else if(ReServedRequestBody.ACTION_GET_AUTO_PACK_MLOT.equals(actionType)){
            List<MaterialLot> materialLotList = gcService.getMaterialLotByPackageRuleAndDocLine(requestBody.getDocLineRrn(), requestBody.getMaterialLotActions(), requestBody.getPackageRule());
            responseBody.setMaterialLotList(materialLotList);
        } else if(ReServedRequestBody.ACTION_GET_PACKED_RULE_LIST.equals(actionType)){
            List<GCProductNumberRelation> boxPackedQtyList = gcService.getProductNumberRelationByDocRrn(requestBody.getDocLineRrn());
            for(GCProductNumberRelation productNumberRelation : boxPackedQtyList) {
                if(StringUtils.YES.equals(productNumberRelation.getDefaultFlag())){
                    responseBody.setDefaultPackedRule(productNumberRelation);
                }
            }
            responseBody.setBoxPackedQtyList(boxPackedQtyList);
        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }

        response.setBody(responseBody);
        return response;
    }
}
