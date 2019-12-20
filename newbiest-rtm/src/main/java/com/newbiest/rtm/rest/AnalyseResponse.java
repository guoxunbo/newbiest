package com.newbiest.rtm.rest;

import com.newbiest.base.msg.Response;
import lombok.Data;

/**
 * Created by guoxunbo on 2017/9/29.
 */
@Data
public class AnalyseResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private AnalyseResponseBody body;
	
}
