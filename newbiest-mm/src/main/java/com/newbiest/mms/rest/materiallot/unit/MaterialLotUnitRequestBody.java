package com.newbiest.mms.rest.materiallot.unit;

import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.MaterialLotUnit;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class MaterialLotUnitRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	private String actionType;

	private List<MaterialLotUnit> MaterialLotUnits;

}
