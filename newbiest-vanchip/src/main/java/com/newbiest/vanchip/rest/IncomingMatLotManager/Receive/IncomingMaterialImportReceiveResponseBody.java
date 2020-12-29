package com.newbiest.vanchip.rest.IncomingMatLotManager.Receive;

import com.newbiest.base.msg.ResponseBody;
import com.newbiest.mms.model.MaterialLot;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class IncomingMaterialImportReceiveResponseBody extends ResponseBody {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "materialLotList")
    private List<MaterialLot> materialLotList ;

}
