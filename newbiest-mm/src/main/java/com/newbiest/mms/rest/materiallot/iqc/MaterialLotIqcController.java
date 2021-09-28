package com.newbiest.mms.rest.materiallot.iqc;

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
public class MaterialLotIqcController extends AbstractRestController {

    @Autowired
    MmsService mmsService;

    @ApiOperation(value = "IQC", notes = "IQC")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "MaterialLotIqcRequest")
    @RequestMapping(value = "/materialLotIQC", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public MaterialLotIqcResponse execute(@RequestBody MaterialLotIqcRequest request) throws Exception {
        MaterialLotIqcResponse response = new MaterialLotIqcResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        MaterialLotIqcResponseBody responseBody = new MaterialLotIqcResponseBody();

        MaterialLotIqcRequestBody requestBody = request.getBody();
        MaterialLotAction materialLotAction = requestBody.getMaterialLotAction();

        MLotCheckSheet mLotCheckSheet = mmsService.iqc(materialLotAction, requestBody.getUrlRemark(), null);

        responseBody.setMaterialLotCheckSheet(mLotCheckSheet);
        response.setBody(responseBody);
        return response;
    }

}
