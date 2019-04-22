import RequestHeader from "../RequestHeader";
const MESSAGE_NAME = "ChangeShiftManage";

export default class ChangeShiftRequestHeader extends RequestHeader{

    constructor() {  
        super(MESSAGE_NAME);
    }

}