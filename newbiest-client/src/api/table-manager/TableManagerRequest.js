import TableManagerRequestBody from "./TableManagerRequestBody";
import TableManagerRequestHeader from "./TableManagerRequestHeader";
import Request from "../Request";
import MessageUtils from "../utils/MessageUtils";
import { UrlConstant } from "../const/ConstDefine";

export default class TableManagerRequest {

    static sendExpTemplate = (object) => {
        let requestBody = TableManagerRequestBody.buildExpTemplate(object.tableRrn);
        let requestHeader = new TableManagerRequestHeader();
        let request = new Request(requestHeader, requestBody, UrlConstant.ExpTemplateUrl);
        let requestObject = {
            request: request,
            success: object.success
        }
        MessageUtils.sendExpRequest(requestObject, object.fileName);
    }
}