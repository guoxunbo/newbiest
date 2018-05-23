package com.newbiest.msg.base.entitylist;

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
public class EntityListRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	private String entityModel;

	private String whereClause;

	private String orderBy;

	private int maxResult;

	private int firstResult;

	private List<String> fields;


}
