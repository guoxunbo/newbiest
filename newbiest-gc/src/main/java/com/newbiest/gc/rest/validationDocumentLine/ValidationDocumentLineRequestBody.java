package com.newbiest.gc.rest.validationDocumentLine;

import com.newbiest.mms.model.DocumentLine;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("具体请求操作信息")
public class ValidationDocumentLineRequestBody extends RequestBody {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "单据详情")
    private List<DocumentLine> documentLines;

    @ApiModelProperty(value = "BOX详情")
    private MaterialLot materialLot;
}
