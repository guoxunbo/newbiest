package com.newbiest.msg.base.entity;

import com.newbiest.base.model.NBBase;
import com.newbiest.msg.RequestBody;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

/**
 * Created by guoxunbo on 2017/9/29.
 */
@Data
public class EntityManagerRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	private String actionType;

	private String entityName;

	private String throwExistRelationException;

	private String entityString;

	public Boolean getThrowExistRelationException() {
		return "Y".equalsIgnoreCase(throwExistRelationException);
	}

	public void setThrowExistRelationException(Boolean throwExistRelationException) {
		this.throwExistRelationException = throwExistRelationException ? "Y" : "N";
	}

}
