package com.newbiest.base.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.newbiest.base.utils.DateUtils;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlElement;


/**
 * 所有需要版本管控的类
 * Created by guoxunbo on 2017/9/7
 */
@MappedSuperclass
@Data
@NoArgsConstructor
public class NBVersionControl extends NBUpdatable {

	private static final long serialVersionUID = 1L;

	public static final String STATUS_FROZNE = "Frozen";
	public static final String STATUS_UNFROZNE = "UnFrozen";
	public static final String STATUS_ACTIVE = "Active";
	public static final String STATUS_INACTIVE = "InActive";
	public static final String STATUS_DELETE = "Delete";

	@Column(name = "NAME")
	@XmlElement(name="Name")
	private String name;

	@Column(name = "DESCRIPTION")
	@XmlElement(name="Description")
	private String description;

	@Column(name="VERSION")
	@XmlElement(name="Version")
	private Long version;

	@Column(name="STATUS")
	@XmlElement(name="Status")
	private String status;

	@Column(name="ACTIVE_TIME")
	@JsonFormat(timezone = GMT_PE,pattern = DateUtils.DEFAULT_DATETIME_PATTERN)
	@XmlElement(name="ActiveTime")
	@Temporal(TemporalType.TIMESTAMP)
	private Date activeTime;

	@Column(name="ACTIVE_USER")
	@XmlElement(name="ActiveUser")
	private String activeUser;

	public String getId() {
		if (version == null) {
			return name;
		} else {
			return name + "." + version;
		}
	}
}
