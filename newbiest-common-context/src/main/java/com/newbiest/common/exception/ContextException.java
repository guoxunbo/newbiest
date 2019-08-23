package com.newbiest.common.exception;

import com.newbiest.base.exception.NewbiestException;

/**
 * Created by guoxunbo on 2018/7/6.
 */
public class ContextException extends NewbiestException {

    public static final String CONTEXT_IS_NOT_EXIST = "common.context_is_not_exist";

    public static final String MERGE_BASIC_OBJ_GET_PROPERTY_ERROR = "com.basic_obj_get_property_error";
    public static final String MERGE_CHECK_OBJ_GET_PROPERTY_ERROR = "com.check_obj_get_property_error";
    public static final String MERGE_SOURCE_VALUE_IS_NOT_SAME_TARGET_VALUE = "com.source_value_is_not_same_target_value";
    public static final String MERGE_UN_SUPPORT_COMPARISON = "com.merge_un_support_comparison";
    public static final String MERGE_RULE_IS_NOT_EXIST = "com.merge_rule_is_not_exist";

}
