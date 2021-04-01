package com.newbiest.vanchip.rest.mlot.iqc;

import com.newbiest.base.msg.ResponseBody;
import com.newbiest.mms.model.MLotCheckSheetLine;
import com.newbiest.mms.model.MaterialLot;
import lombok.Data;

import java.util.List;

@Data
public class MaterialLotIqcResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;


	private List<MaterialLot> dataList;

	private List<MLotCheckSheetLine> mLotCheckSheetLines;

}
