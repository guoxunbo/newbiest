package com.newbiest.security.rest.role;

import com.newbiest.msg.ResponseBody;
import com.newbiest.security.model.NBRole;
import lombok.Data;

/**
 * Created by guoxunbo on 2017/9/29.
 */
@Data
public class RoleResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;
	
	private NBRole nbRole;

}
