// 消息主要发送类
import {UrlConstant} from '../js/const/ConstDefine';
import {RequestHeader} from '../js/request/RequestHeader';
import {Request} from '../js/request/Request';

class MessageUtils{
    static sendJsonMessgae(object) {
        console.log(UrlConstant);
        console.log(RequestHeader);

        let requestUrl = UrlConstant.baseUrl;
        if (object.url) {
            requestUrl = object.url;
        }
        let requestHeader = new RequestHeader("test");
        console.log(requestHeader);
        let request = new Request(requestHeader, "testBody");
        console.log(request);
    }
}
export {MessageUtils};

