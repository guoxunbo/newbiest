package com.newbiest.gc.rest.IncomingMaterialImport.HNWarehouseImport;

import com.newbiest.gc.service.GcService;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.MaterialLotUnit;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/gc")
@Slf4j
public class HNWarehouseImportController {

    @Autowired
    GcService gcService;

    @ApiOperation(value = "湖南仓库导入数据保存", notes = "save")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "HNWarehouseImportRequest")
    @RequestMapping(value = "/HNWarehouseImportSave", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public HNWarehouseImportResponse excute(@RequestBody HNWarehouseImportRequest request)throws Exception {

        HNWarehouseImportResponse response = new HNWarehouseImportResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        HNWarehouseImportResponseBody responseBody = new HNWarehouseImportResponseBody();
        HNWarehouseImportRequestBody requestBody = request.getBody();

        String importType = requestBody.getImportType();
        if(MaterialLotUnit.HN_WAREHOUSE_IMPORT.equals(importType)){
            List<MaterialLot> materialLotList = requestBody.getMaterialLotList();
            gcService.saveHNWarehouseImportList(materialLotList);
        }

        response.setBody(responseBody);
        return response;
    }

}
