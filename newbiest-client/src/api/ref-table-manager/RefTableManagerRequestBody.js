import ReferenceTable from '../dto/ui/ReferenceTable'

const ActionType = {
    GetData: "GetData",
}

export default class RefTableManagerRequestBody {

    actionType;
    referenceTable;
    
    constructor(actionType, referenceTable){
        this.actionType = actionType;
        this.referenceTable = referenceTable;
    }

    static buildGetData(refTableName) {
        let referenceTable = new ReferenceTable(refTableName);
        return new RefTableManagerRequestBody(ActionType.GetData, referenceTable);
    }

}

