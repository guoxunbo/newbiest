import RequestHeader from "../RequestHeader";

const MESSAGE_NAME = "RoleManage";
export default class RoleManagerHeader extends RequestHeader {

    constructor() {  
        super(MESSAGE_NAME);
    }
}