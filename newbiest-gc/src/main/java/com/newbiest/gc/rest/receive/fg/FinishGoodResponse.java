package com.newbiest.gc.rest.receive.fg;

import com.newbiest.msg.Response;
import lombok.Data;

/**
 * Created by guoxunbo on 2017/9/29.
 */
@Data
public class FinishGoodResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private FinishGoodResponseBody body;
	
}
