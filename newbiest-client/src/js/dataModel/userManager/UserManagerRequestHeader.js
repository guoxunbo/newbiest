import { RequestHeader } from "../RequestHeader";
const MESSAGE_NAME = "UserManage";

class UserManagerRequestHeader extends RequestHeader{

    constructor() {  
        super(MESSAGE_NAME);
    }
    
}

export {UserManagerRequestHeader}