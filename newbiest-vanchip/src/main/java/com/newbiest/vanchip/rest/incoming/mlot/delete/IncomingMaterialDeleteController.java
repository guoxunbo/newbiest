package com.newbiest.vanchip.rest.incoming.mlot.delete;

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
@Api(value="/vc", tags="Vanchip客制化")
public class IncomingMaterialDeleteController {

    @Autowired
    VanChipService vanChipService;

    @ApiOperation(value = "来料数据删除", notes = "delete")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "IncomingMaterialImportDeleteRequest")
    @RequestMapping(value = "/IncomingMaterialDelete", method = RequestMethod.POST)
    public IncomingMaterialDeleteResponse excute(@RequestBody IncomingMaterialDeleteRequest request){
        IncomingMaterialDeleteRequestBody requestBody = request.getBody();
        IncomingMaterialDeleteResponse response = new IncomingMaterialDeleteResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        IncomingMaterialDeleteResponseBody responseBody = new IncomingMaterialDeleteResponseBody();

        vanChipService.deleteIncomingMaterialLot(requestBody.getMaterialLotList(), requestBody.getDeleteNote());

        response.setBody(responseBody);
        return response;
    }
}