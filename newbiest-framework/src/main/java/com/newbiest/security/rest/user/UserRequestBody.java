package com.newbiest.security.rest.user;

import com.newbiest.msg.RequestBody;
import com.newbiest.security.model.NBUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by guoxunbo on 2017/9/29.
 */
@Data
@ApiModel("具体请求操作信息")
public class UserRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "操作类型", example = "ChangePassword/RestPassword/GetAuthority/Login等")
	private String actionType;

	@ApiModelProperty(value = "操作的用户对象")
	private NBUser user;

}
