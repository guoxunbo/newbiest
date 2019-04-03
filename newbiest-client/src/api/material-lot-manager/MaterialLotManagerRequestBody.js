import MaterialLot from "../dto/mms/MaterialLot";
import MaterialLotAction from "../dto/mms/MaterialLotAction";
import PropertyUtils from "../utils/PropertyUtils";

const ActionType = {
    Receive2Warehouse: "Receive2Warehouse",
    Consume: "Consume",
}

export default class MaterialLotManagerRequestBody {

    actionType;
    material;

    constructor(actionType, materialLot, materialLotAction){
        this.actionType = actionType;
        this.materialLot = materialLot;
        this.materialLotAction = materialLotAction;
    }

    /**
     * 接收物料批次并入库
     * @param formObject 接收表单对象，页面显示的栏位如果需要保存到后台，都要在materialAction中体现
     */
    static buildReceiveMaterialLot(formObject) {
        // receiveMaterial.materialName, receiveMaterial.warehouseRrn, receiveMaterial.transQty
        let materialLot = new MaterialLot();
        materialLot.setMaterialName(formObject.materialName);
        
        let materialLotAction = new MaterialLotAction();
        PropertyUtils.copyProperties(formObject, materialLotAction);
        // materialLotAction.setTargetWarehouseRrn(warehouseRrn);
        // materialLotAction.setTransQty(qty);
        return new MaterialLotManagerRequestBody(ActionType.Receive2Warehouse, materialLot, materialLotAction);
    }

    /**
     * 处理物料批次相关请求。
     * @param actionObject 
     * @example {action: ActionType.Consume, materialLotAction: {transQty, actionCode...}, materialLotId: "111"}
     */
    static buildHandleMaterialLot(actionObject) {
        let materialLot = actionObject.materialLot;

        let materialLotAction = new MaterialLotAction();
        PropertyUtils.copyProperties(actionObject.materialLotAction, materialLotAction);
        return new MaterialLotManagerRequestBody(actionObject.action, materialLot, materialLotAction);
    }

}

export {ActionType};
