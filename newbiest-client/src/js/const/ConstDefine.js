/**
 * 定义URL 有可能请求多个URL
 */
const UrlConstant = {
    BaseUrl: "http://127.0.0.1:8080/framework/execute"
};

/**
 * 错误码
 */
const ErrorCode = {
    NetworkError: "common.network_error"
};

const EntityModel = {
    NBMessage: "com.newbiest.base.model.NBMessage",
    NBUser: "com.newbiest.security.model.NBUser"
};

/**
 * 成功失败标志位
 */
const ResultIdentify = {
    Success: "SUCCESS",
    Fail: "FAIL",
    Yes: "Y",
    No: "N"
};

const Language = {
    Chinese: "Chinese",
    English: "English"
};

export {UrlConstant, ErrorCode, EntityModel, ResultIdentify, Language};