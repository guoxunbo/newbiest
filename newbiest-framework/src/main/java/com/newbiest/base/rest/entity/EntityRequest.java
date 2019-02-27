package com.newbiest.base.rest.entity;

import com.newbiest.msg.Request;
import lombok.Data;

/**
 * Created by guoxunbo on 2018/7/26.
 */
@Data
public class EntityRequest extends Request {

    /**
     * 针对VersionControl对象
     */
    public static final String ACTION_FROZEN = "Frozen";
    public static final String ACTION_UNFROZEN = "UnFrozen";
    public static final String ACTION_INACTIVE = "Inactive";
    public static final String ACTION_ACTIVE = "Active";

    public static final String MESSAGE_NAME = "EntityManage";

    private EntityRequestBody body;


}
