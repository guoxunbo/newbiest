import MessageUtils from "../utils/MessageUtils";
import DynaxAnalyseRequestHeader from "./DynaxAnalyseRequestHeader";
import Request from "../Request";
import DynaxAnalyseRequestBody from "./DynaxAnalyseRequestBody";
import { UrlConstant } from "../const/ConstDefine";

export default class DynaxAnalyseRequest {

    static sendImportRequest = (object, file) => {
        let requestBody = new DynaxAnalyseRequestBody();
        let requestHeader = new DynaxAnalyseRequestHeader();
        let request = new Request(requestHeader, requestBody, UrlConstant.DynaxAnalyseUrl);
        let requestObject = {
            request: request,
            success: object.success
        }
        MessageUtils.sendImportData(requestObject, file);
    }
}