package com.newbiest.vanchip.rest.doc.check;

import com.newbiest.base.msg.RequestBody;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.DocumentLine;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("具体请求操作信息")
public class CheckMLotRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	private String actionType;

	private DocumentLine documentLine;

	private List<MaterialLotAction> materialLotActionList;

}
