package com.newbiest.base.rest.entityList;

import com.newbiest.base.rest.entity.EntityRequestBody;
import com.newbiest.msg.Request;
import lombok.Data;

/**
 * Created by guoxunbo on 2018/7/31.
 */
@Data
public class EntityListRequest extends Request {

    public static final String MESSAGE_NAME = "GetEntityList";

    private EntityListRequestBody body;
}
