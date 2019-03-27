import RequestHeader from "../RequestHeader";
const MESSAGE_NAME = "MaterialLotInvManage";

export default class MaterialLotInvManagerRequestHeader extends RequestHeader{

    constructor() {
        super(MESSAGE_NAME);
    }

}
