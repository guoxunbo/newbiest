const ActionType = {
    Creata: "Create",
    Update: "Update",
    Delete: "Delete",
    GetByRrn: "GetByRrn",
    GetById: "GetById"
}

const DeleteRelationEntityFlag = {
    Y: "Y",
    N: "N"
}

export default class EntityManagerRequestBody {

    actionType;
    entityModel;
    entityString;
    deleteRelationEntityFlag;

    constructor(actionType, entityModel, entityString, deleteRelationEntityFlag){
        this.actionType = actionType;
        this.entityModel = entityModel;
        this.entityString = entityString;
        this.deleteRelationEntityFlag = deleteRelationEntityFlag;
    }

    static buildDeleteEntity(entityModel, entity, deleteRelationEntityFlag) {
        if (deleteRelationEntityFlag) {
            deleteRelationEntityFlag = true;
        } else {
            deleteRelationEntityFlag = false;
        }
        return new EntityManagerRequestBody(ActionType.Delete, entityModel, JSON.stringify(entity), deleteRelationEntityFlag);
    }

}

