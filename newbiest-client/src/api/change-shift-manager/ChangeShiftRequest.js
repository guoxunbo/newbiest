import ChangeShiftRequestBody from "./ChangeShiftRequestBody";
import ChangeShiftRequestHeader from "./ChangeShiftRequestHeader";
import { UrlConstant } from "../const/ConstDefine";
import Request from "../Request";
import MessageUtils from "../utils/MessageUtils";

export default class ChangeShiftRequest {

    static sendMergeRequest = (object) => {
        if (object.values.newFlag) {
            object.values[DefaultRowKey] = undefined;
        }
        let requestBody = ChangeShiftRequestBody.buildMergeEntity(object.values);
        let requestHeader = new ChangeShiftRequestHeader();
        let request = new Request(requestHeader, requestBody, UrlConstant.ChangeShiftManagerUrl);
        let requestObject = {
            request: request,
            success: object.success
        }
        MessageUtils.sendRequest(requestObject);
    }

    static sendOpenRquest = (object) => {
        let requestBody = ChangeShiftRequestBody.buildOpen(object.changeShift);
        let requestHeader = new ChangeShiftRequestHeader();
        let request = new Request(requestHeader, requestBody, UrlConstant.ChangeShiftManagerUrl);
        let requestObject = {
            request: request,
            success: object.success
        }
        MessageUtils.sendRequest(requestObject);
    }

    static sendCloseRquest = (object) => {
        let requestBody = ChangeShiftRequestBody.buildClose(object.changeShift);
        let requestHeader = new ChangeShiftRequestHeader();
        let request = new Request(requestHeader, requestBody, UrlConstant.ChangeShiftManagerUrl);
        let requestObject = {
            request: request,
            success: object.success
        }
        MessageUtils.sendRequest(requestObject);
    }
}