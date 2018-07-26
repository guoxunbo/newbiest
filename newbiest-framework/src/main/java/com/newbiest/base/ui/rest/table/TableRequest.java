package com.newbiest.base.ui.rest.table;

import com.newbiest.msg.Request;
import lombok.Data;

/**
 * Created by guoxunbo on 2018/7/26.
 */
@Data
public class TableRequest extends Request {

    public static final String MESSAGE_NAME = "TableManage";

    public static final String GET_BY_AUTHORITY = "GetByAuthority";
    public static final String GET_DATA = "GetData";

    private TableRequestBody body;

}
