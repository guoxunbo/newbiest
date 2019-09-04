package com.newbiest.mms.rest.pack.append;

import com.newbiest.mms.model.MaterialLot;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

@Data
public class AppendPackMaterialLotResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	/**
	 * 包装之后产生的新物料批
	 */
	private MaterialLot materialLot;

}
