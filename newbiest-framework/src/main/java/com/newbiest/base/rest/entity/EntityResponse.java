package com.newbiest.base.rest.entity;

import com.newbiest.msg.Response;
import lombok.Data;

/**
 * Created by guoxunbo on 2018/7/26.
 */
@Data
public class EntityResponse extends Response{

    private EntityResponseBody body;
}
