import TableManagerRequestBody from "./TableManagerRequestBody";
import TableManagerRequestHeader from "./TableManagerRequestHeader";
import Request from "../Request";
import MessageUtils from "../utils/MessageUtils";
import { UrlConstant } from "../const/ConstDefine";

export default class TableManagerRequest {

    /**
     * 发送导出数据请求
     * @param object 动态表 @example {tableRrn: 10}
     * @param expTemplate 是否是导出模板标识
     */
    static sendExportRequest = (object, expTemplate) => {
        let requestBody = TableManagerRequestBody.buildExport(object.tableRrn, expTemplate);
        let requestHeader = new TableManagerRequestHeader();
        let request = new Request(requestHeader, requestBody, UrlConstant.ExporttUrl);
        let requestObject = {
            request: request,
            success: object.success
        }
        MessageUtils.sendExpRequest(requestObject, object.fileName);
    }

}