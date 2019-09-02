package com.newbiest.gc.rest.receive.fg;

import com.newbiest.gc.model.MesPackedLot;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Created by guoxunbo on 2017/9/29.
 */
@Data
@ApiModel("具体请求操作信息")
public class FinishGoodRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "GC：Mes完成品批次，必须携带boxId")
	private List<MesPackedLot> mesPackedLots;

}
