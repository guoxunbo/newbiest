import Table from "../dto/ui/Table"

const ActionType = {
    GetByRrn: "GetByRrn",
    GetByName: "GetByName",
    GetData: "GetData",
    ExpTemplate: "ExpTemplate",
    ExpData: "ExpData",
    Import: "Import"
}

export default class TableManagerRequestBody {

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

    static buildGetByName(name) {
        let table = new Table();
        table.setName(name);
        return new TableManagerRequestBody(ActionType.GetByName, table);
    }

    static buildGetDataByName(name, whereClause) {
        let table = new Table();
        table.setName(name);
        table.setWhereClause(whereClause);
        return new TableManagerRequestBody(ActionType.GetData, table);
    }

    static buildGetDataByRrn(objectRrn, whereClause) {
        let table = new Table();
        table.setObjectRrn(objectRrn);
        table.setWhereClause(whereClause);
        return new TableManagerRequestBody(ActionType.GetData, table);
    }
    
    static buildExport(objectRrn, expTemplate) {
        let actionType = ActionType.ExpData;
        if (expTemplate) {
            actionType = ActionType.ExpTemplate;
        }
        let table = new Table();
        table.setObjectRrn(objectRrn);
        return new TableManagerRequestBody(actionType, table);
    }

    static buildImport(objectRrn) {
        let table = new Table();
        table.setObjectRrn(objectRrn);
        return new TableManagerRequestBody(ActionType.Import, table);
    }

}
