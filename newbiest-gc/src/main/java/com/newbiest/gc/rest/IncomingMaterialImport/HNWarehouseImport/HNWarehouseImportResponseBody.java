package com.newbiest.gc.rest.IncomingMaterialImport.HNWarehouseImport;

import com.newbiest.mms.model.MaterialLot;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

import java.util.List;

@Data
public class HNWarehouseImportResponseBody extends ResponseBody {

    private static final long serialVersionUID = 1L;

    private MaterialLot materialLot;

    private List dataList;

    private String importCode;
}
