package com.newbiest.gc.tmp.rest.cp;

import com.newbiest.base.ui.rest.table.TableResponseBody;
import com.newbiest.msg.Response;
import lombok.Data;

/**
 * Created by guoxunbo on 2018/7/26.
 */
@Data
public class TempResponse extends Response {

    private TableResponseBody body;
}
