package com.newbiest.rtm.rest;

import com.newbiest.base.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by guoxunbo on 2017/9/29.
 */
@Data
@ApiModel("具体请求操作信息")
public class AnalyseRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	public static final String MESSAGE_NAME = "AnalyseManage";

}
