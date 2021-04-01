package com.newbiest.vanchip.rest.mlot.weight;

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

@RestController
@RequestMapping("/vc")
@Slf4j
@Api(value="/vc", tags="Vanchip", description = "Vanchip客制化接口")
public class MaterialLotWeightContorller {

    @Autowired
    VanChipService vanChipService;

    @ApiOperation(value = "外箱称重")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "MaterialLotWeightRequest")
    @RequestMapping(value = "/mlotWeight", method = RequestMethod.POST)
    public MaterialLotWeightResponse excute(@RequestBody MaterialLotWeightRequest request)throws Exception {
        MaterialLotWeightRequestBody requestBody = request.getBody();
        MaterialLotWeightResponse response = new MaterialLotWeightResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        MaterialLotWeightResponseBody responseBody = new MaterialLotWeightResponseBody();

        vanChipService.weightMaterialLot(requestBody.getMaterialLotId(), requestBody.getGrossWeight(), requestBody.getCartonSize());

        response.setBody(responseBody);
        return response;
    }
}
