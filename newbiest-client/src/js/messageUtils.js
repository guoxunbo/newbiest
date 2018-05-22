// 消息主要发送类
import {UrlConstant} from '../js/const/ConstDefine';
import {RequestHeader} from '../js/request/RequestHeader';
import {Request} from '../js/request/Request';
import {Notification} from '../js/notice/Notice';
import {AbstractConfirm} from '../js/notice/AbstractConfirm';

class MessageUtils{
    static sendJsonMessgae(object) {
        let requestUrl = UrlConstant.baseUrl;
        if (object.url) {
            requestUrl = object.url;
        }
        // 没有messageName的不允许发送 快速失败
        if (!object.hasOwnProperty("messageName")) {
            
        }
        
        let requestHeader = new RequestHeader(object.messgaeName, this.genId(), 1, "1", "admin");
        console.log(requestHeader);
        let request = new Request(requestHeader, "testBody");
        console.log(request);
    }


    static genId() {
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
            var r = Math.random() * 16 | 0,
            v = c == 'x' ? r : (r & 0x3 | 0x8);
            return v.toString(16);
        }).toUpperCase();
    }
}
export {MessageUtils};

