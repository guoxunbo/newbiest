package com.newbiest.base.exception;

/**
 * 基本框架异常常量Code类
 * Created by guoxunbo on 2017/10/7.
 */
public class NewbiestException {

    // repository相关
    public static final String COMMON_REPOSITORY_IS_NOT_EXIST = "common.repository_is_not_exist";
    // entity相关
    public static final String COMMON_ENTITY_IS_NOT_EXIST = "common.entity_is_not_exist";
    public static final String COMMON_ENTITY_FIELD_IS_NOT_PERSIST = "common.entity_field_is_not_persist";
    public static final String COMMON_RELATION_OBJECT_IS_EXIST = "common.rel_obj_is_exist";
    public static final String COMMON_NONSUPPORT_RELATION_TYPE = "common.nonsupport_rel_type";
    public static final String COMMON_NONSUPPORT_DELETE_ALL_TABLE_DATA = "common.nonsupport_del_all_table_data";

    public static final String COMMON_MODEL_CLASS_LOADER_IS_NOT_EXIST = "common.class_loader_is_not_exist";
    public static final String COMMON_ENTITY_IS_NOT_NEWEST = "common.entity_is_not_newest";

    public static final String COMMON_ENTITY_IS_NOT_VERSIONED = "common.entity_is_not_versioned";
    // query
    public static final String COMMON_QUERY_IS_NOT_EXIST = "common.query_is_not_exist";

    public static final String COMMON_STATUS_IS_NOT_ALLOW = "common.status_is_not_allow";

    public static final String COMMON_UNKNOWN_TIME_UNIT = "common.unknown_time_unit";
    public static final String COMMON_TOKEN_IS_EXPIRED = "common.token_is_expired";
    public static final String COMMON_ORG_IS_NOT_EXIST = "common.org_is_not_exist";
    public static final String COMMON_FILE_IS_NOT_EXIST = "common.file_is_not_exist";
    public static final String COMMON_SYSTEM_OCCURRED_ERROR = "common.system_occurred_error";
    public static final String COMMON_SYSTEM_REQUEST_PARAMETER_ERROR = "common.system_request_parameter_error#%s->#%s";
    public static final String COMMON_SYSTEM_REQUEST_METHOD_ERROR = "common.system_request_method_error#%s";
    public static final String COMMON_OPTIMISTIC_LOCK= "common.optimistic_lock";

    public static final String COMMON_NEW_PASSWORD_IS_NULL = "common.new_pwd_is_null";
    public static final String COMMON_OLD_PASSWORD_IS_INCORRECT = "common.old_pwd_is_incorrect";
    public static final String COMMON_NEW_PASSWORD_EQUALS_OLD_PASSWORD = "common.new_pwd_is_same_as_old_pwd";


}
