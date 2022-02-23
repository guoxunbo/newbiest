package com.newbiest.gc.temp.rest.ft.receive;

import com.newbiest.msg.Response;
import lombok.Data;

/**
 * Created by luoguozhang on 2022/2/22.
 */
@Data
public class TempFtReceiveResponse extends Response {

    private TempFtReceiveResponseBody body;
}
