/**
 * 定义URL 有可能请求多个URL
 */
const ModuleUrlConstant = {
    Framework: "http://127.0.0.1:8080/framework/",
    Security: "http://127.0.0.1:8080/security/",
    UI: "http://127.0.0.1:8080/ui/",
    StatusMachine: "http://127.0.0.1:8080/common/sm/",
    MMS: "http://127.0.0.1:8080/mms/",
    DMS: "http://127.0.0.1:8080/dms/",
    RTM: "http://127.0.0.1:8080/rtm/",
}

const DataBaseType = {
    oracle: "oracle"
}
const DateFormatType = {
    Date: "YYYY-MM-DD",
    DateTime: "YYYY-MM-DD HH:mm:ss",
    Time: "HH:mm:ss"
}

const UrlConstant = {
    UserManagerUrl: ModuleUrlConstant.Security + "userManage",
    TableMangerUrl: ModuleUrlConstant.UI + "tableManage",
    ExporttUrl: ModuleUrlConstant.UI + "export",
    ImportUrl:  ModuleUrlConstant.UI + "importData",
    RefListMangerUrl: ModuleUrlConstant.UI + "refListManage",    
    RefTableManagerUrl: ModuleUrlConstant.UI + "refTableManage",
    EntityManagerUrl: ModuleUrlConstant.Framework + "entityManage",
    RoleManagerUrl: ModuleUrlConstant.Security + "roleManage",
    EntityListManagerUrl: ModuleUrlConstant.Framework + "entityListManage",
    AuthorityManagerUrl: ModuleUrlConstant.Security + "authorityManage",
    StatusModelManagerUrl: ModuleUrlConstant.StatusMachine + "statusModelManage",
    RawMaterialManagerUrl: ModuleUrlConstant.MMS + "rawMaterialManage",
    MaterialLotManagerUrl: ModuleUrlConstant.MMS + "materialLotManage",
    MaterialLotInvManagerUrl: ModuleUrlConstant.MMS + "materialLotInvManage",

    ChangeShiftManagerUrl: ModuleUrlConstant.DMS + "changeShiftManage",
    DynaxAnalyseUrl: ModuleUrlConstant.RTM + "analyseFile"
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

/**
 * 后台返回的错误都不在此处定义由后台返回
 * 定义一些页面错误。比如无法连接后台的错误。
 * 定义一些操作提示。比如操作成功等
 * 定义一些弹出框的名称。比如操作等
 */
const i18N = {
    OperationSucceed: {
        Chinese: "操作成功",
        English: "Operation Succeed",
        Res: ""
    }
};

const EntityModel = {
    NBMessage: "com.newbiest.base.model.NBMessage",
    NBUser: "com.newbiest.security.model.NBUser",
    MaterialEvent: "com.newbiest.mms.state.model.MaterialEvent"
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

/**
 * 所有对象都有自动带出的主键
 */
const DefaultRowKey = "objectRrn";

/**
 * 默认的排序栏位。没手动给值的时候要自动生成值。即表格中数据的最大值加1
 */
const DefaultOrderKey = "seqNo";

//js里面typeof判断所需要的类型
const Type = {
    function: "function"
}

const SqlType = {
    And: " AND ",
    Eq: " = ",
    Where: " WHERE ",
    Like: " LIKE ",
    Gt: " >= ",
    Lt: " <= ",
    toDate: "to_date",
    DateTime: "YYYY-MM-DD HH24:mi:ss",
    Date: "YYYY-MM-DD"
}


export {UrlConstant, DataBaseType, DateFormatType, SystemRefListName, RefTableName, ErrorCode, EntityModel, ResultIdentify, Language, DefaultRowKey, DefaultOrderKey, Type, SqlType, i18N};