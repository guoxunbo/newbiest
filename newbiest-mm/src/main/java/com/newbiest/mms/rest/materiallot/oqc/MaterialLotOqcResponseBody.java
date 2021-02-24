package com.newbiest.mms.rest.materiallot.oqc;

import com.newbiest.base.msg.ResponseBody;
import com.newbiest.mms.model.MLotCheckSheet;
import lombok.Data;

@Data
public class MaterialLotOqcResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private MLotCheckSheet materialLotCheckSheet;


}
