package com.newbiest.rtm.rest;

import com.newbiest.msg.Request;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * Created by guoxunbo on 2019/5/27.
 */
@Data
@ApiModel
public class AnalyseRequest extends Request {

    private AnalyseRequestBody body;

}
