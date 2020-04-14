package com.newbiest.gc.rest.wltBox.print.parameter.wltLot.print.parameter;

import com.newbiest.mms.model.MaterialLotUnit;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;


@Data
@ApiModel("具体请求操作信息")
public class GcGetWltBoxPrintParaRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	private List<MaterialLotUnit> materialLotUnitList;

}
