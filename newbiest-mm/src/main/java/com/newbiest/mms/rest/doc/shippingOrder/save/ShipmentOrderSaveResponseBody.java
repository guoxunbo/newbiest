package com.newbiest.mms.rest.doc.shippingOrder.save;

import com.newbiest.base.msg.ResponseBody;
import com.newbiest.mms.model.DocumentLine;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ShipmentOrderSaveResponseBody extends ResponseBody {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "保存之后数据返回")
    private List<DocumentLine> documentLineList;
}
