package com.newbiest.vanchip.rest.erp.check;

import com.newbiest.base.msg.RequestBody;
import com.newbiest.mms.model.Document;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ErpCreateCheckOrderRequestBody extends RequestBody {

    private static final long serialVersionUID = 1L;

    private String actionType;

    @ApiModelProperty(notes = "单据信息")
    private Document document;

}
