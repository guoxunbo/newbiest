package com.newbiest.msg;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by guoxunbo on 2017/9/29.
 */
@Data
@NoArgsConstructor
@ApiModel(value = "响应的头信息", description = "所有响应都必须要携带")
public class ResponseHeader implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String RESULT_SUCCESS = "SUCCESS";
	public static final String RESULT_FAIL = "FAIL";

	/**
	 * 当返回错误时候消息没找到时，返回此值，代表要去NBMessage中将此值配上
	 */
	public static final Long MESSAGE_NOT_FOUND_RRN = 0L;

	@ApiModelProperty(value = "响应携带的UUID，和request发来的一一对应", required = true)
	private String transactionId;

	@ApiModelProperty(value = "处理结果", required = true, example = "SUCCESS/FAIL")
	private String result = RESULT_SUCCESS;

	@ApiModelProperty(value = "具体的错误码", required = true)
	private String resultCode;

	@ApiModelProperty(value = "错误码对应的中文", required = true)
	private String resultChinese;

	@ApiModelProperty(value = "错误码对应的英文", required = true)
	private String resultEnglish;

	@ApiModelProperty(value = "错误码对应的其他语言", required = true)
	private String resultRes;

	/**
	 * 返回messageRrn. 相当于数字的一个错误码
	 */
	@ApiModelProperty(value = "错误码对应的数据库主键，不存在时为0", required = true)
	private Long messageRrn = MESSAGE_NOT_FOUND_RRN;
}
