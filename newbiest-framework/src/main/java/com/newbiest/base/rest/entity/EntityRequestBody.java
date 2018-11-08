package com.newbiest.base.rest.entity;

import com.newbiest.base.model.NBBase;
import com.newbiest.base.ui.model.NBTable;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by guoxunbo on 2018/7/26.
 */
@Data
@ApiModel
public class EntityRequestBody extends RequestBody {

    private String actionType;

    @ApiModelProperty("类全名，从NBTable上的modelClass带过来")
    private String entityModel;

    @ApiModelProperty("需要操作的entity的具体JSON字符串")
    private String entityString;

    @ApiModelProperty(value = "是否删除连带数据")
    private Boolean deleteRelationEntityFlag;

}
