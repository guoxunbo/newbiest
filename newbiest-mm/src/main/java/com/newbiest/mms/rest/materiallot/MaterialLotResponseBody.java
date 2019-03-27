package com.newbiest.mms.rest.materiallot;

import com.newbiest.mms.model.MaterialLot;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

/**
 * Created by guoxunbo on 2017/9/29.
 */
@Data
public class MaterialLotResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private MaterialLot materialLot;

}
