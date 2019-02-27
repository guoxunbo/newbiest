import RequestHeader from "../RequestHeader";
const MESSAGE_NAME = "RawMaterialManage";

export default class RawMaterialManagerRequestHeader extends RequestHeader{

    constructor() {
        super(MESSAGE_NAME);
    }

}
