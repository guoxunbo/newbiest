import RoleManagerRequestBody from "./RoleManagerRequestBody";

import RoleManagerRequestHeader from "./RoleManagerRequestHeader";
import Request from "../Request";
import { UrlConstant, EntityModel } from "../const/ConstDefine";
import MessageUtils from "../utils/MessageUtils";
import EntityListManagerRequestBody from "../entity-list-manager/EntityListManagerRequestBody";
import EntityListManagerRequestHeader from "../entity-list-manager/EntityListManagerRequestHeader";

import AuthorityManagerRequestBody from "../authority-manager/AuthorityManagerRequestBody";
import AuthorityManagerRequestHeader from "../authority-manager/AuthorityManagerRequestHeader";

export default class RoleManagerRequest {

    static sendGetByRrnRequest = (object) => {
        let requestBody = RoleManagerRequestBody.buildGetByRrnBody(object.roleRrn);
        let requestHeader = new RoleManagerRequestHeader();
        let request = new Request(requestHeader, requestBody, UrlConstant.RoleManagerUrl);
        let requestObject = {
            request: request,
            success: object.success
        }
        
        MessageUtils.sendRequest(requestObject);
    }

    static sendDispatchAuthorityRequest = (object) => {
        let requestBody = RoleManagerRequestBody.buildDispatchAuthorityBody(object);
        let requestHeader = new RoleManagerRequestHeader();
        let request = new Request(requestHeader, requestBody, UrlConstant.RoleManagerUrl);
        let requestObject = {
            request: request,
            success: object.success
        }
        MessageUtils.sendRequest(requestObject);
    }

    static sendDispatchUserRequest = (object) => {
        let requestBody = RoleManagerRequestBody.buildDispatchUserBody(object);
        let requestHeader = new RoleManagerRequestHeader();
        let request = new Request(requestHeader, requestBody, UrlConstant.RoleManagerUrl);
        let requestObject = {
            request: request,
            success: object.success
        }
        MessageUtils.sendRequest(requestObject);
    }

    /**
     * 获取role以及树形菜单。用于分配权限使用
     * @param object 请求参数 
     * @example {roleRrn: 1, success}
     */
    static sendGetRoleByRrnAndGetTreeAuthorityRequest = (object) => {
        let requests = [];
        // 创建role请求
        let requestBody = RoleManagerRequestBody.buildGetByRrnBody(object.roleRrn);
        let requestHeader = new RoleManagerRequestHeader();
        let request = new Request(requestHeader, requestBody, UrlConstant.RoleManagerUrl);
        let requestObject = {
            request: request
        }
        requests.push(requestObject);

        // 创建获取权限树形结构请求
        requestBody = AuthorityManagerRequestBody.buildGetAuthorityTreeBody();
        requestHeader = new AuthorityManagerRequestHeader();
        request = new Request(requestHeader, requestBody, UrlConstant.AuthorityManagerUrl);
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
    
    /**
     * 获取role以及所有用户。用于分配用户使用
     * @param object 请求参数 
     * @example {roleRrn: 1, success}
     */
    static sendGetRoleByRrnAndGetAllUserRequest = (object) => {
        let requests = [];
        // 创建role请求
        let requestBody = RoleManagerRequestBody.buildGetByRrnBody(object.roleRrn);
        let requestHeader = new RoleManagerRequestHeader();
        let request = new Request(requestHeader, requestBody, UrlConstant.RoleManagerUrl);
        let requestObject = {
            request: request
        }
        requests.push(requestObject);

        // 创建获取所有用户请求
        requestBody = EntityListManagerRequestBody.buildGetEntityListBody(EntityModel.NBUser);
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