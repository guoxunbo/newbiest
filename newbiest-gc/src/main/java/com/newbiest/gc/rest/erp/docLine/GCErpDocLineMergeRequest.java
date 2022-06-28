package com.newbiest.gc.rest.erp.docLine;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class GCErpDocLineMergeRequest extends Request {

    public static final String MESSAGE_NAME = "GCErpDocLineMergeManager";

    public static final String ACTION_TYPE_MERGE_DOC = "MergeDoc";

    public static final String ACTION_TYPE_HN_WAREHOUSE_MERGE_DOC =  "HNWarehouseMergeDoc";

    public static final String ACTION_TYPE_BS_WAREHOUSE_MERGE_DOC = "BSWMergeDoc";
    
    public static final String ACTION_TYPE_CANCEL_MERGE_DOC = "UnMergeDoc";

    private GCErpDocLineMergeRequestBody body;
}
