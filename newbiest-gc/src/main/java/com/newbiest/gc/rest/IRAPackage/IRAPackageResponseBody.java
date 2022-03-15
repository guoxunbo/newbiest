package com.newbiest.gc.rest.IRAPackage;

import com.newbiest.mms.model.MaterialLot;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

@Data
public class IRAPackageResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private String materialLotId;

}
