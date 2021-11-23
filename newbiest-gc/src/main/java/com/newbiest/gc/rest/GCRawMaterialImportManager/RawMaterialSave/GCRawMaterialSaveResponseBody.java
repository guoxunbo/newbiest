package com.newbiest.gc.rest.GCRawMaterialImportManager.RawMaterialSave;

import com.newbiest.mms.model.MaterialLot;
import com.newbiest.msg.ResponseBody;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class GCRawMaterialSaveResponseBody extends ResponseBody {

    private static final long serialVersionUID = 1L;

    private String importCode;

    @ApiModelProperty(value = "备料编码")
    private String spareCode;

    private List<MaterialLot> materialLotList;
}
