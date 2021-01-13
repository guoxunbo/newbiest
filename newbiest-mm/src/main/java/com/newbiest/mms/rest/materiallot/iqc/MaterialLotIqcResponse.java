package com.newbiest.mms.rest.materiallot.iqc;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class MaterialLotIqcResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private MaterialLotIqcResponseBody body;
	
}
