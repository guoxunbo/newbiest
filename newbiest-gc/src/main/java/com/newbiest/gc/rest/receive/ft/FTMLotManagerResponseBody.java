package com.newbiest.gc.rest.receive.ft;

import com.newbiest.mms.model.MaterialLotUnit;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

import java.util.List;

/**
 * Created by guozhangLuo on 2020/10/12.
 */
@Data
public class FTMLotManagerResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private List<MaterialLotUnit> materialLotUnitList;

}
