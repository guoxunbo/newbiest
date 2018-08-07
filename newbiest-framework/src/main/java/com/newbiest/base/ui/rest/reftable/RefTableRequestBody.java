package com.newbiest.base.ui.rest.reftable;

import com.newbiest.base.ui.model.NBReferenceTable;
import com.newbiest.base.ui.model.NBTable;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by guoxunbo on 2018/7/26.
 */
@Data
public class RefTableRequestBody extends RequestBody {

    @ApiModelProperty(example = "GetData")
    private String actionType;

    private NBReferenceTable referenceTable;

}
