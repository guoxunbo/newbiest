package com.newbiest.security.rest.role;

import com.newbiest.msg.RequestBody;
import com.newbiest.security.model.NBRole;
import lombok.Data;

/**
 * Created by guoxunbo on 2017/9/29.
 */
@Data
public class RoleRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;
	
	private String actionType;

	private NBRole role;

}
