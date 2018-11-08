import RequestHeader from "../RequestHeader";
const MESSAGE_NAME = "EntityManage";

export default class EntityManagerRequestHeader extends RequestHeader{

    constructor() {  
        super(MESSAGE_NAME);
    }
    
}
