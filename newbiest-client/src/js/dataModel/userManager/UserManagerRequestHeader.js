import { RequestHeader } from "../RequestHeader";
import { MessageName } from "../../const/ConstDefine";

const MESSAGE_NAME = "UserManage";

class UserManagerRequestHeader extends RequestHeader{

    constructor(orgRrn, orgName, userName) {  
        super(MESSAGE_NAME, orgRrn, orgName, userName);
    }
    
}

export {UserManagerRequestHeader}