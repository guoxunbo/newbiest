package com.newbiest.msg.security.role;

import com.newbiest.msg.RequestBody;
import com.newbiest.security.model.NBRole;
import com.newbiest.security.model.NBUser;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by guoxunbo on 2017/9/29.
 */
@Data
public class RoleRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;
	
	private String actionType;

	private NBRole nbRole;

}
