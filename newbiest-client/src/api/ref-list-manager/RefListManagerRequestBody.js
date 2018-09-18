const ActionType = {
    GetData: "GetData",
}

const Category = {
    Owner: "Owner",
    System: "System",
}

export default class RefListManagerRequestBody {

    actionType;
    category;
    referenceName;
    
    constructor(actionType, category, referenceName){
        this.actionType = actionType;
        this.category = category;
        this.referenceName = referenceName;
    }

    static buildOwnerData(referenceName) {
        return new RefListManagerRequestBody(ActionType.GetData, Category.Owner, referenceName);
    }

    static buildSystemData(referenceName) {
        return new RefListManagerRequestBody(ActionType.GetData, Category.System, referenceName);
    }

}

