package com.newbiest.mms.rest.doc.delivery.create;

import com.newbiest.base.msg.RequestBody;
import com.newbiest.mms.model.DocumentLine;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class CreateDeliveryOrderRequestBody extends RequestBody {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "操作类型", example = "create/approve等")
    private String actionType;

    private String documentId;

    private List<DocumentLine> documentLineList;

}
