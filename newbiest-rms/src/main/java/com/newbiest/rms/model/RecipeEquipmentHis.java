package com.newbiest.rms.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.newbiest.base.model.NBBase;
import com.newbiest.base.model.NBHis;
import com.newbiest.base.model.NBVersionControlHis;
import com.newbiest.base.utils.StringUtils;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="RMS_RECIPE_EQUIPMENT_HIS")
@Data
@NoArgsConstructor
public class RecipeEquipmentHis extends NBVersionControlHis {

	private static final long serialVersionUID = 1L;
	
	public static final String TRANS_TYPE_CHANGE_CHECK_SUM = "ChangeCheckSum";
	public static final String TRANS_TYPE_SET_GOLDEN = "SetGolden";
	public static final String TRANS_TYPE_UNSET_GOLDEN = "UnSetGolden";
	public static final String TRANS_TYPE_CHANGE_PATTERN = "ChangePattern";
	public static final String TRANS_TYPE_DOWNLOAD = "Download";
	public static final String TRANS_TYPE_HOLD = "Hold";
	public static final String TRANS_TYPE_RELEASE = "Release";

	@Column(name="RECIPE_EQUIPMENT_RRN")
	private Long recipeEquipmentRrn;

	@Column(name="EQUIPMENT_ID")
	private String equipmentId;

	@Column(name="EQUIPMENT_TYPE")
	private String equipmentType;

	@Column(name="HOLD_STATE")
	private String holdState;

	@Column(name="BODY")
	private String body;

	@Column(name="TIMESTAMP")
	private Date timeStamp;

	@Column(name="CHECK_SUM")
	private String checkSum;
	
	@Column(name="LEVEL_NUMBER")
	private Integer levelNumber;

	@Column(name="GOLDEN_FLAG")
	private String goldenFlag;

	@Column(name="ACTIVE_TYPE")
	private String activeType;

	@Column(name="PATTERN")
	private String pattern;

	/**
	 * 验证Body.此处的Body不是Parameter，而是一个二进制码
	 */
	@Column(name="CHECK_BODY_FLAG")
	private String checkBodyFlag;

	@Column(name="CHECK_SUM_FLAG")
	private String checkSumFlag;

	/**
	 * 验证Parameter
	 */
	@Column(name="CHECK_PARAMETER_FLAG")
	private String checkParameterFlag;

	/**
	 * Download的时候请求的lotId
	 */
	@Column(name="LOT_ID")
	private String lotId;

	/**
	 * Download的时候请求的UNIT_ID比如waferId
	 */
	@Column(name="UNIT_ID")
	private String unitId;

//	@Column(name="PROGRAM_NAME")
//	private String programName;
//
//	@Column(name="PROGRAM_VERSION")
//	private String programVersion;
//
//	@Column(name="PROGRAM_SUFFIX")
//	private String programSuffix;

//	/**
//	 * 文件传输方式(EAP/FTP/SFTP)
//	 */
//	@Column(name="FILE_TRANS_TYPE")
//	private String fileTransType;
//
//	/**
//	 * 从哪个FTP上来
//	 */
//	@Column(name="FROM_FTP_ID")
//	private String fromFtpId;
//
//	@Column(name="FILE_CHECK_TYPE")
//	private String fileCheckType;
//
//	@Column(name="FTP_ID")
//	private String ftpId;
//
//	@Column(name="FULL_PATH")
//	private String fullPath;
//
//	@Column(name="FROM_RECIPE_NAME")
//	private String fromRecipeName;
//
//	@Column(name="FROM_RECIPE_VERSION")
//	private Long fromRecipeVersion;
//
//	@Column(name="FROM_EQUIPMENT_ID")
//	private String fromEquipmentId;
//
//	@Column(name="FROM_EQUIPMENT_TYPE")
//	private String fromEquipmentType;
//
//	@Column(name="FROM_PATTERN")
//	private String fromPattern;

	@Column(name="RESERVED1")
	private String reserved1;
	
	@Column(name="RESERVED2")
	private String reserved2;

	@Column(name="RESERVED3")
	private String reserved3;

	@Column(name="RESERVED4")
	private String reserved4;

	@Column(name="RESERVED5")
	private String reserved5;

	@Override
	public void setNbBase(NBBase base) {
		super.setNbBase(base);
		this.recipeEquipmentRrn = base.getObjectRrn();
	}

	public Boolean getGoldenFlag() {
		return StringUtils.YES.equalsIgnoreCase(this.goldenFlag) ? true : false;
	}

	public void setGoldenFlag(Boolean goldenFlag) {
		this.goldenFlag = goldenFlag ? StringUtils.YES : StringUtils.NO;
	}

	public Boolean getCheckBodyFlag() {
		return StringUtils.YES.equalsIgnoreCase(checkBodyFlag);
	}

	public void setCheckBodyFlag(Boolean checkBodyFlag) {
		this.checkBodyFlag = checkBodyFlag ? StringUtils.YES : StringUtils.NO;
	}

	public Boolean getCheckSumFlag() {
		return StringUtils.YES.equalsIgnoreCase(checkSumFlag);
	}

	public void setCheckSumFlag(Boolean checkSumFlag) {
		this.checkSumFlag = checkSumFlag ? StringUtils.YES : StringUtils.NO;
	}

	public Boolean getCheckParameterFlag() {
		return StringUtils.YES.equalsIgnoreCase(checkParameterFlag);
	}

	public void setCheckParameterFlag(Boolean checkParameterFlag) {
		this.checkParameterFlag = checkParameterFlag ? StringUtils.YES : StringUtils.NO;
	}

}
