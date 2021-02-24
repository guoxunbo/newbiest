package com.newbiest.mms.rest.materiallot.oqc;

import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.MLotCheckSheet;
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

@RestController
@RequestMapping("/mms")
@Slf4j
@Api(value="/mms", tags="MaterialManagerSystem", description = "物料管理相关")
public class MaterialLotOqcController extends AbstractRestController {

    @Autowired
    MmsService mmsService;

    @ApiOperation(value = "OQC", notes = "OQC")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "MaterialLotOqcRequest")
    @RequestMapping(value = "/materialLotOQC", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public MaterialLotOqcResponse execute(@RequestBody MaterialLotOqcRequest request) throws Exception {
        MaterialLotOqcResponse response = new MaterialLotOqcResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        MaterialLotOqcResponseBody responseBody = new MaterialLotOqcResponseBody();

        MaterialLotOqcRequestBody requestBody = request.getBody();
        MaterialLotAction materialLotAction = requestBody.getMaterialLotAction();

        MLotCheckSheet mLotCheckSheet = mmsService.oqc(materialLotAction);

        responseBody.setMaterialLotCheckSheet(mLotCheckSheet);
        response.setBody(responseBody);
        return response;
    }

}
