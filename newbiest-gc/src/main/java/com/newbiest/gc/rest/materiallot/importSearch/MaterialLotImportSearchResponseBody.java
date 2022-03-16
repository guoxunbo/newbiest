package com.newbiest.gc.rest.materiallot.importSearch;

import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.MaterialLotUnit;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

import java.util.List;

@Data
public class MaterialLotImportSearchResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private List<MaterialLot> materialLotList;

	private List<MaterialLotUnit> materialLotUnitList;

}
