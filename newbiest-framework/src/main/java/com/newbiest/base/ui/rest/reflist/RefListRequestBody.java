package com.newbiest.base.ui.rest.reflist;

import com.newbiest.base.ui.model.NBReferenceTable;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by guoxunbo on 2018/7/26.
 */
@Data
public class RefListRequestBody extends RequestBody {

    private String actionType;

    @ApiModelProperty(value = "类别。系统栏位参考值还是用户栏位参考值", example = "Owner/System")
    private String category;

    @ApiModelProperty("动态栏位参考名称")
    private String referenceName;


}
