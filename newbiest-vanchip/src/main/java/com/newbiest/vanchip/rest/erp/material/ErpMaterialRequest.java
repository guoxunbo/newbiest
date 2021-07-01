package com.newbiest.vanchip.rest.erp.material;

import com.newbiest.base.msg.Request;
import lombok.Data;

@Data
public class ErpMaterialRequest extends Request {

    public static final String MESSAGE_NAME = "ErpMaterialManager";

    private ErpMaterialRequestBody body;
}
