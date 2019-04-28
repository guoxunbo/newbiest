
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
        return new ChangeShiftRequestBody(actionType, changeShift);
    }

    static buildOpen(changeShift) {
        return new ChangeShiftRequestBody(ActionType.Open, changeShift);
    }

    static buildClose(changeShift) {
        return new ChangeShiftRequestBody(ActionType.Close, changeShift);
    }
    
}