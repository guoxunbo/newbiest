package com.newbiest.msg.base.entitylist;

import com.newbiest.msg.Request;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by guoxunbo on 2017/9/29.
 */
@XmlRootElement(name = "Request")
@XmlAccessorType(XmlAccessType.FIELD)
public class EntityListRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "GetEntityList";

	@XmlElement(name="Body")
	private EntityListRequestBody body;

	public EntityListRequestBody getBody() {
		return body;
	}

	public void setBody(EntityListRequestBody body) {
		this.body = body;
	}
}
