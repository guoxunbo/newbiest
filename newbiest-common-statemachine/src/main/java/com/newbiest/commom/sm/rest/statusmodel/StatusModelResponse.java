package com.newbiest.commom.sm.rest.statusmodel;

import com.newbiest.msg.Response;
import lombok.Data;

/**
 * Created by guoxunbo on 2017/9/29.
 */
@Data
public class StatusModelResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private StatusModelResponseBody body;

}
