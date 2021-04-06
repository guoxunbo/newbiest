package com.newbiest.mms.rest.materiallot.split.standard;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class SplitStandardMaterialLotResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private SplitStandardMaterialLotResponseBody body;
	
}
