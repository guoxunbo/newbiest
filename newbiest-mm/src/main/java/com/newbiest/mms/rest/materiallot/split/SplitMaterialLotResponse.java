package com.newbiest.mms.rest.materiallot.split;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class SplitMaterialLotResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private SplitMaterialLotResponseBody body;
	
}
