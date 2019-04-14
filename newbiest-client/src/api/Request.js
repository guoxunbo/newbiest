import {UrlConstant} from "./const/ConstDefine"

export default class Request{

    constructor(requestHeader, requestBody, url) {
        this.header = requestHeader;
        this.body = requestBody;
        if (!url) {
            url = UrlConstant.BaseUrl;
        } else {
            this.url = url;
        }
    }
    
}