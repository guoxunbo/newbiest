package com.newbiest.vanchip.rest.mlot.inventory;

import com.newbiest.base.msg.ResponseBody;
import com.newbiest.mms.model.MaterialLotInventory;
import lombok.Data;

import java.util.List;


@Data
public class MaterialLotInventoryResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private MaterialLotInventory materialLotInventory;

	private List<MaterialLotInventory> materialLotInventorys;

}
