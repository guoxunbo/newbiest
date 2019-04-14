import UserManagerRequestBody from "./UserManagerRequestBody";
import UserManagerRequestHeader from "./UserManagerRequestHeader";
import Request from "../Request";
import { UrlConstant } from "../const/ConstDefine";
import MessageUtils from "../utils/MessageUtils";


/**
 * 用户相关请求
 */
export default class UserManagerRequest {

    static sendLoginRequest = (object) => {
        let user = object.user;
        let requestBody = UserManagerRequestBody.buildLoginRequestBody(user.username, user.password);
        let requestHeader = new UserManagerRequestHeader();
        requestHeader.orgRrn = user.org;
        requestHeader.language = user.language;

        let request = new Request(requestHeader, requestBody, UrlConstant.UserManagerUrl);
        let requestObject = {
            request: request,
            success: object.success
        }
        MessageUtils.sendRequest(requestObject);
    }

    static sendChangePassword = (object) => {
        let requestBody = UserManagerRequestBody.buildChangePwdBody(object.username, object.password, object.newPassword);
        let requestHeader = new UserManagerRequestHeader();
        let request = new Request(requestHeader, requestBody, UrlConstant.UserManagerUrl);
        let requestObject = {
            request: request,
            success: object.success
        }
        MessageUtils.sendRequest(requestObject);
    }

    static sendResetPassword = (object) => {
        let requestBody = UserManagerRequestBody.buildResetPwdBody(object.username);
        let requestHeader = new UserManagerRequestHeader();
        let request = new Request(requestHeader, requestBody, UrlConstant.UserManagerUrl);
        let requestObject = {
            request: request,
            success: object.success
        }
        MessageUtils.sendRequest(requestObject);
    }
    
    static sendMergeUserRequest = (object) => {
        let requestBody = UserManagerRequestBody.buildMergeUserBody(object.user);
        let requestHeader = new UserManagerRequestHeader();
        let request = new Request(requestHeader, requestBody, UrlConstant.UserManagerUrl);
        let requestObject = {
            request: request,
            success: object.success
        }
        MessageUtils.sendRequest(requestObject);
    }
    
}