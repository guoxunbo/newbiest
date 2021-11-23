package com.newbiest.gc.rest.scm.engManager;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class EngManagerRequest extends Request {

    public static final String MESSAGE_NAME = "ScmEngManager";

    public static final String ACTION_TYPE_SAVE = "Save";

    public static final String ACTION_TYPE_UPDATE = "Update";

    public static final String ACTION_TYPE_DELETE = "Delete";

    private EngManagerRequestBody body;
}
