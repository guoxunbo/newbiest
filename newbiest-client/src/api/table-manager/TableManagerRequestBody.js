import Table from "../dto/ui/Table"

const ActionType = {
    GetByRrn: "GetByRrn",
    GetData: "GetData",
}
class TableManagerRequestBody {

    actionType;
    table;

    constructor(actionType, table){
        this.actionType = actionType;
        this.table = table;
    }

    static buildGetByRrn(objectRrn) {
        let table = new Table();
        table.setObjectRrn(objectRrn);
        return new TableManagerRequestBody(ActionType.GetByRrn, table);
    }

    static buildGetData(objectRrn) {
        let table = new Table();
        table.setObjectRrn(objectRrn);
        return new TableManagerRequestBody(ActionType.GetData, table);
    }
}

export {TableManagerRequestBody}