package com.newbiest.gc.rest.unConfirmWaferSet;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * Created by guozhangLuo on 2020/12/11
 */
@Data
@ApiModel
public class UnConfirmWaferSetRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "UnConfirmWaferManager";

	private UnConfirmWaferSetRequestBody body;

}
