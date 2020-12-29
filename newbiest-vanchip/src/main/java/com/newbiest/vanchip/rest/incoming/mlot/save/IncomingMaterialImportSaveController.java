package com.newbiest.vanchip.rest.incoming.mlot.save;

import com.newbiest.mms.model.MaterialLot;
import com.newbiest.vanchip.service.VanChipService;
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
public class IncomingMaterialImportSaveController {

    @Autowired
    VanChipService vanChipService;

    @ApiOperation(value = "来料导入数据保存", notes = "save")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "IncomingMaterialImportSaveRequest")
    @RequestMapping(value = "/IncomingMaterialSave", method = RequestMethod.POST)
    public IncomingMaterialImportSaveResponse excute(@RequestBody IncomingMaterialImportSaveRequest request)throws Exception {
        IncomingMaterialImportSaveRequestBody requestBody = request.getBody();
        IncomingMaterialImportSaveResponse response = new IncomingMaterialImportSaveResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        IncomingMaterialImportSaveResponseBody responseBody = new IncomingMaterialImportSaveResponseBody();

        List<MaterialLot> materialLotList = requestBody.getMaterialLotList();
        vanChipService.importIncomingOrder(materialLotList);
        response.setBody(responseBody);
        return response;
    }
}
