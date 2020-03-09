package com.newbiest.gc.rest.IncomingMaterialImport.IncomingMaterialSave;

import com.newbiest.mms.model.MaterialLot;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

import java.util.List;

@Data
public class IncomingMaterialSaveResponseBody extends ResponseBody {

    private static final long serialVersionUID = 1L;

    private MaterialLot materialLot;

    private List dataList;

    private String importCode;
}
