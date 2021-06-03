package com.newbiest.vanchip.rest.finishGood.reserved;

import com.newbiest.base.msg.Request;
import lombok.Data;

@Data
public class FinishGoodReservedRequest extends Request {

    public static final String MESSAGE_NAME = "FinishGoodManager";

    public static final String ACTION_TYPE_GET_MATERIALLOT = "GetMaterialLot";
    public static final String ACTION_TYPE_FINISH_GOOD_RESERVED = "FinishGoodReserved";
    public static final String ACTION_TYPE_FINISH_GOOD_UN_RESERVED = "FinishGoodUnReserved";
    public static final String ACTION_TYPE_PRINT_RESERVED_ORDER = "PrintReservedOrder";
    public static final String ACTION_TYPE_GET_RESERVED_MLOT_BY_STANDARD_QTY = "GetReservedMLotByStandardQty";

    private FinishGoodReservedRequestBody body;
}
