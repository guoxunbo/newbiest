package com.newbiest.vanchip.rest.doc.finishGood.create;

import com.newbiest.base.msg.RequestBody;
import com.newbiest.mms.model.MaterialLot;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class CreateFinishGoodOrderRequestBody extends RequestBody {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "单据号")
    private String documentId;

    @ApiModelProperty(value = "mes完成品ReelCode")
    private List<MaterialLot> materialLots;
}
