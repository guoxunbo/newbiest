/**
 * 定义URL 有可能请求多个URL
 */
const ModuleUrlConstant = {
    Security: "http://127.0.0.1:8080/security/",
    UI: "http://127.0.0.1:8080/ui/"
}
const UrlConstant = {
    BaseUrl: "http://127.0.0.1:8080/framework/execute",
    UserManagerUrl: ModuleUrlConstant.Security + "userManage",
    TableMangerUrl: ModuleUrlConstant.UI + "tableManage",
    RefListMangerUrl: ModuleUrlConstant.UI + "refListManage",    
    RefTableManagerUrl: ModuleUrlConstant.UI + "refTableManage"
};

const SystemRefListName = {
    Language: "Language"
};

const RefTableName = {
    NBOrg: "NBOrg"
};
/**
 * 错误码 需要前端自行国际化
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

const DefaultRowKey = "objectRrn";

//js里面typeof判断所需要的类型
const Type = {
    function: "function"
}

export {UrlConstant, SystemRefListName, RefTableName, ErrorCode, EntityModel, ResultIdentify, Language, DefaultRowKey, Type};