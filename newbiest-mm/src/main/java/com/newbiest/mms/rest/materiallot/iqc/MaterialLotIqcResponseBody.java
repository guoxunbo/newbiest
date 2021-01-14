package com.newbiest.mms.rest.materiallot.iqc;

import com.newbiest.base.msg.ResponseBody;
import com.newbiest.mms.model.MLotCheckSheet;
import com.newbiest.mms.model.MaterialLot;
import lombok.Data;

@Data
public class MaterialLotIqcResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private MLotCheckSheet materialLotCheckSheet;


}
