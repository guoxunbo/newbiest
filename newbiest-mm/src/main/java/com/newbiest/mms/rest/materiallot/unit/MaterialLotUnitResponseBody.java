package com.newbiest.mms.rest.materiallot.unit;

import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.MaterialLotUnit;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

import java.util.List;


@Data
public class MaterialLotUnitResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private List<MaterialLotUnit> materialLotUnits;

}
