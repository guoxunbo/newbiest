const ActionType = {
    Creata: "Create",
    Update: "Update"
}

export default class RawMaterialManagerRequestBody {

    actionType;
    material;

    constructor(actionType, material){
        this.actionType = actionType;
        this.material = material;
    }

    static buildMergeRawMaterial(rawMaterial) {
        let actionType;
        if (rawMaterial.objectRrn) {
            actionType = ActionType.Update;
        } else {
            actionType = ActionType.Creata;
        }
        return new RawMaterialManagerRequestBody(actionType, rawMaterial);
    }

}

