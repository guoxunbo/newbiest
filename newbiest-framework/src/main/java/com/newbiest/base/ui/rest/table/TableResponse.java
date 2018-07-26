package com.newbiest.base.ui.rest.table;

import com.newbiest.msg.Response;
import lombok.Data;

/**
 * Created by guoxunbo on 2018/7/26.
 */
@Data
public class TableResponse extends Response{

    private TableResponseBody body;
}
