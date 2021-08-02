package com.newbiest.mms.rest.materiallot.inv;

import com.newbiest.base.msg.ResponseBody;
import com.newbiest.mms.model.MaterialLotInventory;
import lombok.Data;

/**
 * Created by guoxunbo on 2017/9/29.
 */
@Data
public class MaterialLotInvResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private MaterialLotInventory materialLotInventory;

}
