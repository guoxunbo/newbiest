import { RequestHeader } from "../RequestHeader";
import { MessageName } from "../../const/ConstDefine";

const MESSAGE_NAME = "GetEntityList";

class EntityListRequestHeader extends RequestHeader{

    constructor(orgRrn, orgName, userName){  
        super(MESSAGE_NAME, orgRrn, orgName, userName);
    }
    
}

export {EntityListRequestHeader}