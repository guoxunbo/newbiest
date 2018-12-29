import {ErrorCode, ResultIdentify, Language, i18N} from '../const/ConstDefine';
import {Notification} from '../../components/notice/Notice';

import {Response} from "../Response";
import {ResponseHeader} from "../ResponseHeader";

import axios from "axios";
import { SessionContext } from '../Application';

/**
 *  消息主要发送类
 */
export default class MessageUtils {
    
    /**
     * 同时发送2个请求，并且都处理完毕一起返回
     * 常用场景比如用户组获取用户和获取所有用户一起使用
     * @param requestObject
     * @example {requests: [request1, request2...], success:}
     */
    static sendTwoRequest(requestObject) {
        let self = this;
        let requests = requestObject.requests;
        if (Array.isArray(requests)) {
            let axioses = [];
            requests.forEach((requestObject)=>{
                let request = requestObject.request;
                axioses.push(axios.post(request.url, request));
            });
            axios.all(axioses).then(axios.spread(function(responseValue1, responseValue2) {
                // 处理2个reponse 都成功才回调Object.success
                let response1 = new Response(responseValue1.data.header, responseValue1.data.body);
                let response2 = new Response(responseValue2.data.header, responseValue2.data.body);
                if (ResultIdentify.Fail == response1.header.result) {
                    self.handleException(response1.header);
                    return;
                } 
                if (ResultIdentify.Fail == response2.header.result) {
                    self.handleException(response2.header);
                    return;
                } 
                if (requestObject.success) {
                    requestObject.success(response1.body, response2.body);
                } else {
                    this.showOperationSuccess();
                }
            })).catch(function(exception) {
                self.handleException(exception);
            });
        } else {

        }
    }

    /**
     * 
     */
    judgeResponse(object, requestObject) {
        let response = new Response(object.data.header, object.data.body);
        if (ResultIdentify.Fail == response.header.result) {
            this.handleException(response.header);
        } else {
            if (requestObject.success != undefined) {
                requestObject.success(response.body);
            } else {
                this.showOperationSuccess();
            }
        }
    }

    /**
     * 发送异步请求
     */
    static sendRequest(requestObject) {
        let self = this;
        let request = requestObject.request;
        axios.post(request.url, request).then(function(object) {
            let response = new Response(object.data.header, object.data.body);
            if (ResultIdentify.Fail == response.header.result) {
                self.handleException(response.header);
            } else {
                if (requestObject.success != undefined) {
                    requestObject.success(response.body);
                } else {
                    self.showOperationSuccess();
                }
            }
        }).catch(function(exception) {
            self.handleException(exception);
        }); 
    }
    
    static showOperationSuccess = () => {
        let language = SessionContext.getLanguage();
        let notice = "";
        if (language == undefined) {
            language == Language.Chinese;
        }
        if (language == Language.Chinese) {
            notice = i18N.OperationSucceed.Chinese;
        } else if (language == Language.English) {
            notice = i18N.OperationSucceed.English;
        } else {
            notice = i18N.OperationSucceed.Res;
        }
        Notification.showSuccess(notice);
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

