import { RequestHeader } from "../RequestHeader";

const MESSAGE_NAME = "GetEntityList";

class EntityListRequestHeader extends RequestHeader{

    constructor(){  
        super(MESSAGE_NAME);
    }
    
}

export {EntityListRequestHeader}