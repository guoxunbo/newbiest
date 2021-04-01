package com.newbiest.vanchip.rest.unpack;

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

/**
 * 物料批次拆包装
 */
@RestController("VCUnPackMaterialLotController")
@RequestMapping("/vc")
@Slf4j
@Api(value="/vc", tags="vanchip客制化")
public class UnPackMaterialLotController extends AbstractRestController {

    @Autowired
    VanChipService vanChipService;

    @ApiOperation(value = "物料批次拆包", notes = "物料批次拆包")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "UnPackMaterialLotRequest")
    @RequestMapping(value = "/unPackMaterialLots", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public UnPackMaterialLotResponse execute(@RequestBody UnPackMaterialLotRequest request) throws Exception {
        UnPackMaterialLotResponse response = new UnPackMaterialLotResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        UnPackMaterialLotResponseBody responseBody = new UnPackMaterialLotResponseBody();
        UnPackMaterialLotRequestBody requestBody = request.getBody();

        List<MaterialLot> unpackedMainMaterialLots = vanChipService.unPack(requestBody.getMaterialLotActions());
        responseBody.setMaterialLots(unpackedMainMaterialLots);
        response.setBody(responseBody);
        return response;
    }

}
