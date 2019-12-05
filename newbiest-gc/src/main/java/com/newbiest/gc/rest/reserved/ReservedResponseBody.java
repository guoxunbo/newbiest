package com.newbiest.gc.rest.reserved;

import com.newbiest.mms.model.MaterialLot;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

import java.util.List;

@Data
public class ReservedResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private List<MaterialLot> materialLotList;
}
