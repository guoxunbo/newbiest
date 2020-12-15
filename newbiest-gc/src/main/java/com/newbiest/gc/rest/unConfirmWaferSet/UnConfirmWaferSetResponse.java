package com.newbiest.gc.rest.unConfirmWaferSet;

import com.newbiest.msg.Response;
import lombok.Data;

/**
 * Created by guozhangLuo on 2020/12/11
 */
@Data
public class UnConfirmWaferSetResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private UnConfirmWaferSetResponseBody body;
	
}
