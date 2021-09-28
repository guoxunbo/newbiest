package com.newbiest.vanchip.rest.doc.scrap;

import com.google.common.collect.Lists;
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

@RestController
@RequestMapping("/vc")
@Slf4j
@Api(value="/vc", tags="Vanchip客制化")
public class ScrapMLotController extends AbstractRestController {

    @Autowired
    VanChipService vanChipService;

    @ApiOperation(value = "报废")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "ScrapMLotRequest")
    @RequestMapping(value = "/scrapMLotManager", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public ScrapMLotResponse execute(@RequestBody ScrapMLotRequest request) throws Exception {
        ScrapMLotResponse response = new ScrapMLotResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        ScrapMLotResponseBody responseBody = new ScrapMLotResponseBody();
        ScrapMLotRequestBody requestBody = request.getBody();

        String actionTyep = requestBody.getActionType();
        List<MaterialLot> materialLotList = requestBody.getMaterialLotList();
        String documentId = requestBody.getDocumentId();
        if (ScrapMLotRequest.ACTION_TYPE_GET_RESERVED_MLOT.equals(actionTyep)){
            List<MaterialLot> materialLots = vanChipService.getReservedMLotByOrder(documentId);

            responseBody.setMaterialLotList(materialLots);
        } else if (ScrapMLotRequest.ACTION_TYPE_SCRAP_MLOT_BY_ORDER.equals(actionTyep)){

            vanChipService.scrapMLot(documentId, materialLotList);
        } else if (ScrapMLotRequest.ACTION_VALIDATE_RESERVED_RULE.equals(actionTyep)){

            MaterialLot materialLot = vanChipService.validateReservedMLot(documentId, materialLotList.get(0).getMaterialLotId());
            List<MaterialLot> materialLots = Lists.newArrayList();
            materialLots.add(materialLot);
            responseBody.setMaterialLotList(materialLots);
        }else {
            throw new ClientParameterException(Request.NON_SUPPORT_ACTION_TYPE, actionTyep);
        }

        response.setBody(responseBody);
        return response;
    }

}
