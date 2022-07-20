package com.newbiest.gc.rest.vbox.print.parameter;

import com.newbiest.mms.model.MaterialLot;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import java.util.List;


@Data
@ApiModel("具体请求操作信息")
public class GetVboxPrintParaRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	private List<MaterialLot> materialLotList;

	private String actionType;

	private Long tableRrn;

	private String vboxId;
}
