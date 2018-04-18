package com.newbiest.msg.base.entitylist;

import com.newbiest.msg.Response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by guoxunbo on 2017/9/29.
 */
@XmlRootElement(name = "Response")
@XmlAccessorType(XmlAccessType.FIELD)
public class EntityListResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	@XmlElement(name="Body")
	private EntityListResponseBody body;

	public EntityListResponseBody getBody() {
		return body;
	}

	public void setBody(EntityListResponseBody body) {
		this.body = body;
	}
	
}
