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

    constructor(actionType, entityModel, entity, deleteRelationEntityFlag){
        this.actionType = actionType;
        this.entityModel = entityModel;
        this.entityString = JSON.stringify(entity);
        this.deleteRelationEntityFlag = deleteRelationEntityFlag;
    }

    static buildMergeEntity(entityModel, entity) {
        let actionType;
        if (entity.objectRrn) {
            actionType = ActionType.Update;         
        } else {
            actionType = ActionType.Creata;         
        }
        return new EntityManagerRequestBody(actionType, entityModel, entity);
    }

    static buildDeleteEntity(entityModel, entity, deleteRelationEntityFlag) {
        if (!deleteRelationEntityFlag) {
            deleteRelationEntityFlag = false;
        }
        return new EntityManagerRequestBody(ActionType.Delete, entityModel, entity, deleteRelationEntityFlag);
    }

}

