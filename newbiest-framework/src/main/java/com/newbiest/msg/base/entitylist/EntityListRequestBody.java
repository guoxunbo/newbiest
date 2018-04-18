package com.newbiest.msg.base.entitylist;

import com.newbiest.msg.RequestBody;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

/**
 * Created by guoxunbo on 2017/9/29.
 */
@XmlAccessorType(XmlAccessType.NONE)
public class EntityListRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@XmlElement(name="EntityModel")
	private String entityModel;

	@XmlElement(name="WhereClause")
	private String whereClause;

	@XmlElement(name="OrderBy")
	private String orderBy;

	@XmlElement(name="MaxResult")
	private int maxResult;

	@XmlElement(name="FirstResult")
	private int firstResult;

	@XmlElementWrapper(name="FieldList")
	@XmlElement(name="Field")
	private List<String> fields;

	public String getEntityModel() {
		return entityModel;
	}

	public void setEntityModel(String entityModel) {
		this.entityModel = entityModel;
	}

	public String getWhereClause() {
		return whereClause;
	}

	public void setWhereClause(String whereClause) {
		this.whereClause = whereClause;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public int getMaxResult() {
		return maxResult;
	}

	public void setMaxResult(int maxResult) {
		this.maxResult = maxResult;
	}

	public int getFirstResult() {
		return firstResult;
	}

	public void setFirstResult(int firstResult) {
		this.firstResult = firstResult;
	}

	public List<String> getFields() {
		return fields;
	}

	public void setFields(List<String> fields) {
		this.fields = fields;
	}
}
