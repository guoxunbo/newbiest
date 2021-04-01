package com.newbiest.vanchip.rest.doc.finishGood.receive;

import com.newbiest.base.msg.ResponseBody;
import com.newbiest.mms.model.MaterialLot;
import lombok.Data;

import java.util.List;

@Data
public class ReceiveFinishGoodResponseBody extends ResponseBody {

    private static final long serialVersionUID = 1L;

    private List<MaterialLot> materialLotList;
}
