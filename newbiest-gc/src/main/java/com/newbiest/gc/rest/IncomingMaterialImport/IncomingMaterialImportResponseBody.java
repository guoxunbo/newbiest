package com.newbiest.gc.rest.IncomingMaterialImport;

import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.MaterialLotUnit;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

import java.util.List;

@Data
public class IncomingMaterialImportResponseBody extends ResponseBody {

    private static final long serialVersionUID = 1L;

    private List dataList;

    private String bondedProperty;
}
