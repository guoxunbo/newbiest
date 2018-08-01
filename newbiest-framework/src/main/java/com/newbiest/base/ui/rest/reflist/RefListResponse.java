package com.newbiest.base.ui.rest.reflist;

import com.newbiest.msg.Response;
import lombok.Data;

/**
 * Created by guoxunbo on 2018/7/26.
 */
@Data
public class RefListResponse extends Response{

    private RefListResponseBody body;
}
