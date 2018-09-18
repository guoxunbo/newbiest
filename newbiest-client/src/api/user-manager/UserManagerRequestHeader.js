import RequestHeader from "../RequestHeader";
const MESSAGE_NAME = "UserManage";

export default class UserManagerRequestHeader extends RequestHeader{

    constructor() {  
        super(MESSAGE_NAME);
    }
    
}

