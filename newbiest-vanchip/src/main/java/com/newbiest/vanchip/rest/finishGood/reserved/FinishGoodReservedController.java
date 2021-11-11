package com.newbiest.vanchip.rest.finishGood.reserved;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.msg.Request;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.service.DocumentService;
import com.newbiest.vanchip.service.VanChipService;
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

@RestController
@RequestMapping("/vc")
@Slf4j
@Api(value="/vc", tags="vc客制化接口", description = "VanChip客制化")
public class FinishGoodReservedController {

    @Autowired
    VanChipService vanChipService;

    @Autowired
    DocumentService documentService;

    @ApiOperation(value = "对完成品做操作", notes = "备货")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "ImportExcelRequest")
    @RequestMapping(value = "/finishGoodReserved", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public FinishGoodReservedResponse execute(@RequestBody FinishGoodReservedRequest request) throws Exception {
        FinishGoodReservedResponse response = new FinishGoodReservedResponse();
        FinishGoodReservedResponseBody responseBody = new FinishGoodReservedResponseBody();
        FinishGoodReservedRequestBody requestBody = request.getBody();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        String actionType = requestBody.getActionType();

        if (FinishGoodReservedRequest.ACTION_TYPE_GET_MATERIALLOT.equals(actionType)){
            List<MaterialLot> materialLots = vanChipService.getReservedMaterialLot(requestBody.getDocumentLine());
            materialLots = materialLots.stream().filter(mLot -> MaterialLot.HOLD_STATE_OFF.equals(mLot.getHoldState())).collect(Collectors.toList());
            responseBody.setMaterialLotList(materialLots);
        }else if (FinishGoodReservedRequest.ACTION_TYPE_FINISH_GOOD_RESERVED.equals(actionType)){

            vanChipService.reservedMaterialLot(requestBody.getDocumentLine(), requestBody.getMaterialLotActionList());
        }else if (FinishGoodReservedRequest.ACTION_TYPE_FINISH_GOOD_UN_RESERVED.equals(actionType)){

            vanChipService.unReservedMaterialLot(requestBody.getMaterialLotActionList());
        }else if (FinishGoodReservedRequest.ACTION_TYPE_PRINT_RESERVED_ORDER.equals(actionType)){

            List<MaterialLot> materialLotList = vanChipService.printReservedOrder(requestBody.getDocumentLine());
            responseBody.setMaterialLotList(materialLotList);
        }else if(FinishGoodReservedRequest.ACTION_TYPE_GET_RESERVED_MLOT_BY_STANDARD_QTY.equals(actionType)){

            List<MaterialLot> reservedMLotByStandardQty = vanChipService.getReservedMLotByStandardQty(requestBody.getDocumentLine(), requestBody.getStandardQty());
            responseBody.setMaterialLotList(reservedMLotByStandardQty);
        }else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + actionType);
        }

        response.setBody(responseBody);
        return response;
    }
}
