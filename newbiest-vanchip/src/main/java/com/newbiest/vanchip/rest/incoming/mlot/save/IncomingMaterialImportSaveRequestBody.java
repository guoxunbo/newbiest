package com.newbiest.vanchip.rest.incoming.mlot.save;

import com.newbiest.base.msg.RequestBody;
import com.newbiest.mms.model.MaterialLot;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("具体请求操作信息")
public class IncomingMaterialImportSaveRequestBody extends RequestBody {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "来料信息")
    private List<MaterialLot> materialLotList;

}
