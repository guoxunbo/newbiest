package com.newbiest.gc.rest.erp.docLine;

import com.newbiest.mms.model.DocumentLine;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("具体请求操作信息")
public class GCErpDocLineMergeRequestBody extends RequestBody {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "操作类型")
    private String actionType;

    @ApiModelProperty(value = "单据信息")
    private List<DocumentLine> documentLines;

}
