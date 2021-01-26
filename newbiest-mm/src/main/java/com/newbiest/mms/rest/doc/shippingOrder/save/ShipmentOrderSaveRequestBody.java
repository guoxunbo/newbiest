package com.newbiest.mms.rest.doc.shippingOrder.save;

import com.newbiest.base.msg.RequestBody;
import com.newbiest.mms.model.DocumentLine;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ShipmentOrderSaveRequestBody extends RequestBody {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "待保存的数据")
    private List<DocumentLine> documentLineList;
}
