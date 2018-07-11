package com.newbiest.rms.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue("PROGRAM")
@Data
public class RecipeEquipmentProgram extends AbstractRecipeEquipment {

	private static final long serialVersionUID = 1L;

	public static final String PROGRAM_PATH_PROD = "PROD";
	
	public static final String FILE_CHECK_TYPE_LOCAL = "Local";
	public static final String FILE_CHECK_TYPE_FTP = "FTP";
	public static final String FILE_CHECK_TYPE_SFTP = "SFTP";

	public static final String TRANS_TYPE_EAP = "EAP";
	public static final String TRANS_TYPE_FTP = "FTP";
	public static final String TRANS_TYPE_SFTP = "SFTP";

	public static final String DEFAULT_FTP_ID = "Default";

	/**
	 * 相同名称的只能有一个(不包括程序版本及后缀)
	 */
	public static final int FILE_STYLE_ONLY_BY_NAME = 1 << 0;

	/**
	 * 程序的不同目录下的规则
	 */
	public static Map<String, Integer> statusStyles = new HashMap<String, Integer>();
	
	static {
		statusStyles.put(PROGRAM_PATH_PROD, FILE_STYLE_ONLY_BY_NAME);
	}
	
	/**
	 * 文件名称
	 */
	@Column(name="PROGRAM_NAME")
	private String programName;

	/**
	 * 文件版本
	 */
	@Column(name="PROGRAM_VERSION")
	private String programVersion;

	/**
	 * 后缀
	 */
	@Column(name="PROGRAM_SUBFIX")
	private String programSubfix;
 
	/**
	 * 检查文件存在类型
	 */
	@Column(name="FILE_CHECK_TYPE")
	private String fileCheckType = FILE_CHECK_TYPE_LOCAL;

	/**
	 * 传输类型(EAP/FTP/SFTP)
	 */
	@Column(name="FILE_TRANS_TYPE")
	private String fileTransType;
	
	/**
	 * FTP ID
	 */
	@Column(name="FTP_ID")
	private String ftpId = DEFAULT_FTP_ID;
	
	/**
	 * 全路径
	 */
	@Column(name="FULL_PATH")
	private String fullPath;
	
	@Column(name="FROM_FTP_ID")
	private String fromFtpId;

}
