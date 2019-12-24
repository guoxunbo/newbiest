package com.newbiest.gc.rest.validateMLotReserved;

import com.newbiest.mms.model.MaterialLot;
import com.newbiest.msg.RequestBody;
import lombok.Data;

@Data
public class ValidationMLotReservedRequestBody extends RequestBody {

    private static final long serialVersionUID = 1L;

    private MaterialLot materialLot;
}
