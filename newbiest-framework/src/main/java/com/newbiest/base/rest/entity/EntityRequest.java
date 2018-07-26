package com.newbiest.base.rest.entity;

import com.newbiest.msg.Request;
import lombok.Data;

/**
 * Created by guoxunbo on 2018/7/26.
 */
@Data
public class EntityRequest extends Request {

    public static final String MESSAGE_NAME = "EntityManage";

    private EntityRequestBody body;

}
