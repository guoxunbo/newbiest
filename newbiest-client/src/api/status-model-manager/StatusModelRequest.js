import StatusModelRequestBody from "./StatusModelRequestBody";
import StatusModelRequestHeader from "./StatusModelRequestHeader";
import { UrlConstant } from "../const/ConstDefine";
import EntityListManagerRequestBody from "../entity-list-manager/EntityListManagerRequestBody";
import EntityListManagerRequestHeader from "../entity-list-manager/EntityListManagerRequestHeader";
import Request from "../Request";
import MessageUtils from "../utils/MessageUtils";

export default class StatusModelRequest {

    static sendDispatchEventRequest = (object) => {
        let requestBody = StatusModelRequestBody.buildDispatchEventBody(object);
        let requestHeader = new StatusModelRequestHeader();
        let request = new Request(requestHeader, requestBody, UrlConstant.StatusModelManagerUrl);
        let requestObject = {
            request: request,
            success: object.success
        }
        MessageUtils.sendRequest(requestObject);
    }

    /**
     * 获取statusModel以及所有事件。用于分配事件使用
     *  因为statusModel会有很多不同的类别，比如物料的，设备的，载具的，故此处需要传递具体的eventModelClass
     * @param object 请求参数 
     * @example {stausModelRrn: 1, eventModelClass:"com.newbiest.mms.state.model.MaterialEvent", success}
     */
    static sendGetRoleByRrnAndGetAllEventRequest = (object) => {
        let requests = [];
        // 创建role请求
        let requestBody = StatusModelRequestBody.buildGetByRrnBody(object.statusModelRrn);
        let requestHeader = new StatusModelRequestHeader();
        let request = new Request(requestHeader, requestBody, UrlConstant.StatusModelManagerUrl);
        let requestObject = {
            request: request
        }
        requests.push(requestObject);

        // 创建获取所有用户请求
        requestBody = EntityListManagerRequestBody.buildGetEntityListBody(object.eventModelClass);
        requestHeader = new EntityListManagerRequestHeader();
        request = new Request(requestHeader, requestBody, UrlConstant.EntityListManagerUrl);
        requestObject = {
            request: request
        }
        requests.push(requestObject);

        let messageObject = {
            requests: requests,
            success: object.success
        }
        MessageUtils.sendTwoRequest(messageObject);
    }
}