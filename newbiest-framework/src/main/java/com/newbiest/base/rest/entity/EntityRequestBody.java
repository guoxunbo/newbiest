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
    private String modelClass;

    @ApiModelProperty("需要操作的entity的具体JSON字符串")
    private String entityString;

    @ApiModelProperty(value = "需要操作的entity的具体JSON", example = "Y/N")
    private String throwExistRelationException;

    public Boolean getThrowExistRelationException() {
        return StringUtils.YES.equalsIgnoreCase(throwExistRelationException);
    }

    public void setThrowExistRelationException(Boolean throwExistRelationException) {
        this.throwExistRelationException = throwExistRelationException ? StringUtils.YES : StringUtils.NO;
    }
}
