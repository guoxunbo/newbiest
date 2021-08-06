package com.newbiest.vanchip.rest.erp.order;

import com.newbiest.base.msg.RequestBody;
import com.newbiest.mms.model.Document;
import com.newbiest.mms.model.MaterialLot;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ErpCreateOrderRequestBody extends RequestBody {

    private static final long serialVersionUID = 1L;

    private String actionType;

    @ApiModelProperty(notes = "单据信息")
    private Document document;

    @ApiModelProperty(notes = "物料信息")
    private List<MaterialLot> materialLotList;
}
