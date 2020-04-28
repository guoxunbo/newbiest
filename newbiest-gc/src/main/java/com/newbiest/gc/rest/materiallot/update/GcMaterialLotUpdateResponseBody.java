package com.newbiest.gc.rest.materiallot.update;


import com.newbiest.mms.model.MaterialLot;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

@Data
public class GcMaterialLotUpdateResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private MaterialLot materialLot;

}
