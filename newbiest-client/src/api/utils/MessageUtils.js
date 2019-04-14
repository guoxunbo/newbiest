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
     * 发送导入请求
     *  因为json似乎是没有表达内嵌文件的格式.所有需要封装formData进行提交
     * @param requestObject {url:"", request:{param1, param2},}
     * @param file 文件
     */
    static sendImportData(requestObject, file) {
        let self = this;
        let request = requestObject.request;
        let formData = new FormData();
        formData.append("file", file);
        formData.append("request", JSON.stringify(request));

        axios.post(request.url, formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
        }).then(function(object) {
            let response = new Response(object.data.header, object.data.body);
            if (ResultIdentify.Fail == response.header.result) {
                self.handleException(response.header);
            } else {
                if (requestObject.success) {
                    requestObject.success(response.body);
                } else {
                    self.showOperationSuccess();
                }
            }
        }).catch(function(exception) {
            self.handleException(exception);
        }); 

    }

    /**
     * 发送导出数据请求比如导出excel 具体类型由contnt-type决定
     * 因为导出的时候不要返回体的。只需返回字节流即可
     * @param requestObject {url:"", request:{param1, param2},}
     * @param fileName 文件名字
     */
    static sendExpRequest(requestObject, fileName) {
        let self = this;
        let request = requestObject.request;
        axios.post(request.url, request, {
            responseType: 'blob'
        }).then(function(object) {
            let type = object.headers['content-type'];

            let blob = new Blob([object.data], { type: type}); 
            let elink = document.createElement('a');
            elink.download = fileName;
            elink.style.display = 'none';
            elink.href = URL.createObjectURL(blob);
            document.body.appendChild(elink);
            elink.click();
            document.body.removeChild(elink);

            self.showOperationSuccess();
        }).catch(function(exception) {
            self.handleException(exception);
        }); 
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
                if (requestObject.success) {
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
        if (!language) {
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
        if (!language) {
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

