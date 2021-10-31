package com.newbiest.vanchip.rest.mlot.iqc;

import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.msg.Request;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.mms.model.MaterialLot;
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

@RestController("VCMaterialLotIqcController")
@RequestMapping("/vc")
@Slf4j
@Api(value="/vc", tags="vanChip客制化")
public class MaterialLotIqcController extends AbstractRestController {

    @Autowired
    VanChipService vanChipService;

    @ApiOperation(value = "IQC", notes = "批量IQC")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "MaterialLotIqcRequest")
    @RequestMapping(value = "/materialLotIQC", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public MaterialLotIqcResponse execute(@RequestBody MaterialLotIqcRequest request) throws Exception {
        MaterialLotIqcResponse response = new MaterialLotIqcResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        MaterialLotIqcResponseBody responseBody = new MaterialLotIqcResponseBody();

        MaterialLotIqcRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();
        List<String> materialLotIds = requestBody.getMaterialLotIds();

        if (MaterialLotIqcRequest.ACTIONT_BATCH_IQC.equals(actionType)){

            vanChipService.batchIqc(requestBody.getMaterialLotActions(), requestBody.getUrlRemark(), requestBody.getCheckSheetLineList());
        }else if (MaterialLotIqcRequest.ACTION_VALIDATION_AND_GET_MLOT.equals(actionType)){

            List<MaterialLot> dataList = vanChipService.validationAndGetWaitIqcMLot(materialLotIds);
            responseBody.setDataList(dataList);
        } else if (MaterialLotIqcRequest.ACTIONT_IQC_APPROVAL.equals(actionType)){

            vanChipService.iqcApprove(requestBody.getMaterialLotActions());
        } else if (MaterialLotIqcRequest.ACTIONT_START_IQC.equals(actionType)){

            vanChipService.startIqc(materialLotIds);
        }else {
            throw new ClientParameterException(Request.NON_SUPPORT_ACTION_TYPE, actionType);
        }
        
        response.setBody(responseBody);
        return response;
    }

}
