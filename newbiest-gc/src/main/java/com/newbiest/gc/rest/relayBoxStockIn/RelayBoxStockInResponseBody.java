package com.newbiest.gc.rest.relayBoxStockIn;

import com.newbiest.mms.model.MaterialLot;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

import java.util.List;

@Data
public class RelayBoxStockInResponseBody extends ResponseBody {

    private static final long serialVersionUID = 1L;

    private MaterialLot materialLot;

    private List<MaterialLot> materialLots;

}
