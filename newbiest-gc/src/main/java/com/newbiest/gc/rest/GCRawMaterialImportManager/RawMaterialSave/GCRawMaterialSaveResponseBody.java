package com.newbiest.gc.rest.GCRawMaterialImportManager.RawMaterialSave;

import com.newbiest.mms.model.MaterialLot;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

import java.util.List;

@Data
public class GCRawMaterialSaveResponseBody extends ResponseBody {

    private static final long serialVersionUID = 1L;

    private String importCode;

    private List<MaterialLot> materialLotList;
}
