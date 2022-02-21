package com.newbiest.gc.temp.rest.ft;

import com.newbiest.base.ui.rest.table.TableResponseBody;
import com.newbiest.msg.Response;
import lombok.Data;

/**
 * Created by luoguozhang on 2022/2/11.
 */
@Data
public class TempFtResponse extends Response {

    private TableResponseBody body;
}
