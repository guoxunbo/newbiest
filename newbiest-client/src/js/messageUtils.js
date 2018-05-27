import {UrlConstant, ErrorCode, ResultIdentify, Language} from '../js/const/ConstDefine';
import {RequestHeader} from '../js/dataModel/RequestHeader';
import {Request} from '../js/dataModel/Request';
import {Notification} from '../js/notice/Notice';

import {EntityListRequestHeader} from "../js/dataModel/entityList/EntityListRequestHeader";
import {EntityListRequestBody} from "../js/dataModel/entityList/EntityListRequestBody";
import {EntityModel} from "../js/const/ConstDefine"
import {Response} from "../js/dataModel/Response";
import {ResponseHeader} from "../js/dataModel/ResponseHeader";

import {JsonUtils} from "../js/JsonUtils"

import axios from "axios";
import qs from "qs";

/**
 *  消息主要发送类
 */
class MessageUtils {

    static getEntityList() {
        let requestHeader = new EntityListRequestHeader(0, "11", "admin");
        let requestBody = new EntityListRequestBody();
        requestBody.setEntityModel(EntityModel.NBUser);
        let request = new Request(requestHeader, requestBody);
        
        let object = {
            request: request
        }
        this.sendRequest(object)
    }

    static sendRequest(object) {
        let self = this;
        let requestUrl = UrlConstant.BaseUrl;
        if (object.url != undefined) {
            requestUrl = object.url;
        }
        let parameters = new URLSearchParams();
        parameters.append("request", JsonUtils.object2Json(object.request));
        axios.post(requestUrl, parameters).then(function(object) {
            let response = new Response(object.data.header, object.data.body);
            if (ResultIdentify.Fail == response.header.result) {
                self.handleException(response.header);
            } else {
                if (object.success != undefined) {
                    object.success();
                } else {
                    Notification.showSuccess("操作成功")
                }
                //TODO 操作成功之后如何处理
                console.log(response.body);
            }
        }).catch(function(exception) {
            self.handleException(exception);
        }); 

    }

    static handleException(exception) {
        let error = "";
        let errroCode = 0;
        //TODO 处理国际化
        let language = Language.Chinese;
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
            // String的不是后台的错误 需要重新去查询NBMessage返回
            if (exception == "Error: Network Error") {
                error = ErrorCode.NetworkError;
            }  
        }
        
        Notification.showError(errroCode, error);
    }
}
export {MessageUtils};

