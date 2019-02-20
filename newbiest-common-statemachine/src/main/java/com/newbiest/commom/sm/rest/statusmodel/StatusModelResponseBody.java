package com.newbiest.commom.sm.rest.statusmodel;

import com.newbiest.commom.sm.model.StatusModel;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

/**
 * Created by guoxunbo on 2017/9/29.
 */
@Data
public class StatusModelResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;
	
	private StatusModel statusModel;

}
