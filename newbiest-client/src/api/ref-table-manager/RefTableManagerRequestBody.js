import ReferenceTable from '../dto/ui/ReferenceTable'

const ActionType = {
    GetData: "GetData",
}

export default class RefTableManagerRequestBody {

    actionType;
    referenceTable;
    parameters;

    constructor(actionType, referenceTable, parameters){
        this.actionType = actionType;
        this.referenceTable = referenceTable;
        this.parameters = parameters;
    }

    static buildGetData(refTableName, parameters) {
        let referenceTable = new ReferenceTable(refTableName);
        return new RefTableManagerRequestBody(ActionType.GetData, referenceTable, parameters);
    }

}

