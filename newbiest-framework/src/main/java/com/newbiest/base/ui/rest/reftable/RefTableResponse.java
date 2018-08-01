package com.newbiest.base.ui.rest.reftable;

import com.newbiest.msg.Response;
import lombok.Data;

/**
 * Created by guoxunbo on 2018/7/26.
 */
@Data
public class RefTableResponse extends Response{

    private RefTableResponseBody body;
}
