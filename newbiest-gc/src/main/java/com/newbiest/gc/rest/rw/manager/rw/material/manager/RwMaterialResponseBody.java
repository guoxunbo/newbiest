package com.newbiest.gc.rest.rw.manager.rw.material.manager;

import com.newbiest.mms.model.MaterialLot;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

import java.util.List;

@Data
public class RwMaterialResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private MaterialLot materialLot;

	private List<MaterialLot> materialLotList;

	private String materialLotId;

}
