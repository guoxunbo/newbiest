package com.newbiest.mms.rest.pack;

import com.newbiest.mms.model.MaterialLot;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

@Data
public class PackMaterialLotResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	/**
	 * 包装之后产生的新物料批
	 */
	private MaterialLot materialLot;

}
