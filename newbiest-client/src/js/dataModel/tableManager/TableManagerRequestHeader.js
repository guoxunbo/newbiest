import { RequestHeader } from "../RequestHeader";
const MESSAGE_NAME = "TableManager";

class TableManagerRequestHeader extends RequestHeader{

    constructor() {  
        super(MESSAGE_NAME);
    }
    
}

export {TableManagerRequestHeader}