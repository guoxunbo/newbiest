package com.newbiest.rms.exception;

import com.newbiest.base.exception.NewbiestException;

/**
 * 所有异常的定义的地方
 * Created by guoxunbo on 2018/7/4.
 */
public class RmsException extends NewbiestException{

    public static final String NONSUPPORT_EXPIRED_POLICY = " rms.nonsupport.expired.policy";

    public static final String RECIPE_PARAMETER_NOT_EXPECT = " rms.eqp_recipe_parameter_not_expect";
    public static final String COMPARE_RECIPE_PARAMETER_IS_NOT_EXIST = "rms.compare_recipe_parameter_is_not_exist";
    public static final String RECIPE_PARAMETER_NOT_SAME = "rms.recipe_parameter_not_same";
    public static final String RECIPE_PARAMETER_NOT_NUMBER_FORMAT = "rms.recipe_parameter_not_number_format";
    public static final String RECIPE_PARAMETER_VALUE_IS_NOT_EXIST = "rms.recipe_parameter_value_is_not_exist";
    public static final String RECIPE_PARAMETER_NOT_IN_RANGE = "rms.recipe_parameter_not_in_range";

    public static final String EQP_IS_NOT_EXIST = "rms.eqp_is_not_exist";
    public static final String EQP_GOLDEN_RECIPE_IS_MULTI = " rms.eqp_golden_recipe_is_multi";
    public static final String EQP_RECIPE_DELETE_ONLY_UNFROZEN_OR_INACTIVE = "rms.delete_only_unfrozen_or_inactive";
    public static final String EQP_RECIPE_IS_GOLDEN_RECIPE = "rms.eqp_recipe_is_golden_recipe";
    public static final String EQP_RECIPE_GOLDEN_RECIPE_IS_EXIST = "rms.eqp_golden_recipe_is_exist";
    public static final String EQP_RECIPE_IS_NOT_ACTIVE = "rms.eqp_recipe_is_not_active";
    public static final String EQP_RECIPE_IS_NOT_GOLDEN = "rms.eqp_recipe_is_not_golden";
    public static final String EQP_RECIPE_IS_ACTIVE = "rms.eqp_recipe_is_active";
    public static final String EQP_RECIPE_IS_ALREADY_HOLD = "rms.eqp_recipe_is_hold";
    public static final String EQP_RECIPE_IS_ALREADY_RELEASE = "rms.eqp_recipe_is_release";
    public static final String EQP_RECIPE_ONLINE_MULTI_CHANGE = "rms.online_multi_change";
    public static final String EQP_RECIPE_IS_NOT_EXIST = "rms.eqp_recipe_is_not_exist";

    public static final String EQP_RECIPE_BODY_NOT_SAME = "rms.eqp_recipe_body_not_same";
    public static final String EQP_RECIPE_BODY_IS_NOT_EXIST = "rms.eqp_recipe_body_is_not_exist";

    public static final String EQP_RECIPE_TIMESTAMP_NOT_SAME = "rms.eqp_recipe_timestamp_not_same";
    public static final String EQP_RECIPE_TIMESTAMP_IS_NOT_EXIST = "rms.eqp_recipe_timestamp_is_not_exist";
    public static final String EQP_RECIPE_CHECKSUM_NOT_SAME = "rms.eqp_recipe_checksum_not_same";
    public static final String EQP_RECIPE_CHECKSUM_IS_NOT_EXIST = "rms.eqp_recipe_checksum_is_not_exist";
    public static final String EQP_RECIPE_STATE_ON_HOLD = "rms.eqp_recipe_state_on_hold";
}
