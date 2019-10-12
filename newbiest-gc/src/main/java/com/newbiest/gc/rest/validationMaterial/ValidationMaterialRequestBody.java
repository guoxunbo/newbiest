package com.newbiest.gc.rest.validationMaterial;

import com.newbiest.mms.model.MaterialLot;
import com.newbiest.msg.RequestBody;
import lombok.Data;

@Data
public class ValidationMaterialRequestBody extends RequestBody {

    private static final long serialVersionUID = 1L;

    private MaterialLot materialLot;

    private MaterialLot materialLotFirst;
}
