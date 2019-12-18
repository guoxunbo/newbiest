package com.newbiest.gc.rest.weight;

import com.newbiest.mms.model.MaterialLot;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

@Data
public class WeightResponseBody extends ResponseBody {

    private static final long serialVersionUID = 1L;

    private MaterialLot materialLot;
}
