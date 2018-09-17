import { RequestHeader } from "../../js/dataModel/RequestHeader";
const MESSAGE_NAME = "RefTableManager";

export default class RefTableManagerRequestHeader extends RequestHeader{

    constructor() {  
        super(MESSAGE_NAME);
    }
    
}
