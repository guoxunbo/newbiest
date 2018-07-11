import {UrlConstant, ErrorCode, ResultIdentify, Language} from '../js/const/ConstDefine';
import {Request} from '../js/dataModel/Request';
import {Notification} from '../js/notice/Notice';

import {EntityListRequestHeader} from "../js/dataModel/entityList/EntityListRequestHeader";
import {EntityListRequestBody} from "../js/dataModel/entityList/EntityListRequestBody";
import {Response} from "../js/dataModel/Response";
import {ResponseHeader} from "../js/dataModel/ResponseHeader";

import {JsonUtils} from "../js/JsonUtils"

import axios from "axios";
import { SessionContext } from './Application';

/**
 *  消息主要发送类
 */
class MessageUtils {
    
    static getEntityList(entityModel) {
        let requestHeader = new EntityListRequestHeader();
        let requestBody = new EntityListRequestBody();
        requestBody.setEntityModel(entityModel);
        let request = new Request(requestHeader, requestBody);
        
        let object = {
            request: request
        }
        this.sendRequest(object)
    }

    static sendRequest(requestObject) {
        let self = this;
        let requestUrl = UrlConstant.BaseUrl;
        if (requestObject.url != undefined) {
            requestUrl = requestObject.url;
        }
        let parameters = new URLSearchParams();
        parameters.append("request", JsonUtils.object2Json(requestObject.request));
        axios.post(requestUrl, parameters).then(function(object) {
            let response = new Response(object.data.header, object.data.body);
            if (ResultIdentify.Fail == response.header.result) {
                self.handleException(response.header);
            } else {
                if (requestObject.success != undefined) {
                    requestObject.success(response.body);
                } else {
                    Notification.showSuccess("操作成功")
                }
            }
        }).catch(function(exception) {
            self.handleException(exception);
        }); 

    }

    static handleException(exception) {
        let error = "";
        let errroCode = 0;
        let language = SessionContext.getLanguage();
        if (language == undefined) {
            language = Language.Chinese;
        }
        if (exception instanceof ResponseHeader) {
            if (language == Language.Chinese) {
                error = exception.resultChinese;
            } else if (language == Language.English) {
                error = exception.resultEnglish;
            }
            if (error == null || error == "") {
                error = exception.resultCode;
            }
            errroCode = exception.messageRrn;
        } else {
            // String的不是后台的错误 需要去加载Client端的i18N信息
            if (exception == "Error: Network Error") {
                error = ErrorCode.NetworkError;
            } else {
                error = exception;
            }
        }
        
        Notification.showError(errroCode, error);
    }
}
export {MessageUtils};

