package com.newbiest.gc.rest.vbox.print.parameter;

import com.newbiest.gc.model.MesPackedLot;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;


@Data
@ApiModel("具体请求操作信息")
public class GetVboxPrintParaRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	private List<MesPackedLot> mesPackedLots;

	private String actionType;

	private Long tableRrn;

	private String vboxId;
}
