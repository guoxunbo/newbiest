package com.newbiest.gc.rest.IncomingMaterialImport.HNWarehouseImport;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class HNWarehouseImportRequest  extends Request {

    public static final String MESSAGE_NAME = "GCHNWarehouseImport";

    private HNWarehouseImportRequestBody body;

}
