package com.newbiest.mms.rest.materiallot.split.standard;

import com.newbiest.base.msg.ResponseBody;
import com.newbiest.mms.model.MaterialLot;
import lombok.Data;

import java.util.List;

@Data
public class SplitStandardMaterialLotResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private List<MaterialLot> subMaterialLots;

}
