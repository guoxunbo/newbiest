package com.newbiest.base.rest.entityList;

import com.newbiest.msg.Response;
import lombok.Data;

/**
 * Created by guoxunbo on 2018/7/31.
 */
@Data
public class EntityListResponse extends Response {

    private static final long serialVersionUID = 1L;

    private EntityListResponseBody body;
}
