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

	public static final String STATUS_FROZEN = "Frozen";
	public static final String STATUS_UNFROZEN = "UnFrozen";
	public static final String STATUS_ACTIVE = "Active";
	public static final String STATUS_INACTIVE = "InActive";
	public static final String STATUS_DELETE = "Delete";

	@Column(name = "NAME")
	private String name;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name="VERSION")
	private Long version;

	@Column(name="STATUS")
	private String status;

	@Column(name="ACTIVE_TIME")
	@JsonFormat(timezone = GMT_PE,pattern = DateUtils.DEFAULT_DATETIME_PATTERN)
	@Temporal(TemporalType.TIMESTAMP)
	private Date activeTime;

	@Column(name="ACTIVE_USER")
	private String activeUser;

	public String getId() {
		if (version == null) {
			return name;
		} else {
			return name + "." + version;
		}
	}
}
