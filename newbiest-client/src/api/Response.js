import {ResponseHeader  } from "./ResponseHeader";
class Response{

    constructor(responseHeader, responseBody) {
        this.header = new ResponseHeader(responseHeader);
        this.body = responseBody;
    }
    
}
export {Response};