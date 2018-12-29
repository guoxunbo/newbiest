import RequestHeader from "../RequestHeader";
const MESSAGE_NAME = "EntityListManage";

export default class EntityListManagerRequestHeader extends RequestHeader{

    constructor() {  
        super(MESSAGE_NAME);
    }

}