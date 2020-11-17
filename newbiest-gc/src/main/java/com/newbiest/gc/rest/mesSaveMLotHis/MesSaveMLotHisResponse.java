package com.newbiest.gc.rest.mesSaveMLotHis;

import com.newbiest.msg.Response;
import lombok.Data;


@Data
public class MesSaveMLotHisResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private MesSaveMLotHisResponseBody body;

	private String message;
}
