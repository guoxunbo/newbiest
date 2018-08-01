package com.newbiest.base.ui.rest.reftable;

import com.newbiest.msg.Request;
import lombok.Data;

/**
 * Created by guoxunbo on 2018/7/26.
 */
@Data
public class RefTableRequest extends Request {

    public static final String MESSAGE_NAME = "RefTableManage";

    public static final String GET_DATA = "GetData";

    private RefTableRequestBody body;

}
