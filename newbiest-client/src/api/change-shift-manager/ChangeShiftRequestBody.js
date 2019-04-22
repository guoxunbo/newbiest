
const ActionType = {
    Close: "Close",
    Open: "Open",
    Create: "Create",
    Update: "Update"
}
export default class ChangeShiftRequestBody {
    actionType;
    changeShift;

    constructor(actionType, changeShift) {
        this.actionType = actionType;
        this.changeShift = changeShift;
    }
    
    static buildMergeEntity(changeShift) {
        let actionType;
        if (changeShift.objectRrn) {
            actionType = ActionType.Update;         
        } else {
            actionType = ActionType.Create;         
        }
        debugger;
        return new ChangeShiftRequestBody(actionType, changeShift);
    }

}