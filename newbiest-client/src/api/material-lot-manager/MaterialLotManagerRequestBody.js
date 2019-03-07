import MaterialLot from "../dto/mms/MaterialLot";
import MaterialLotAction from "../dto/mms/MaterialLotAction";

const ActionType = {
    Receive2Warehouse: "Receive2Warehouse",
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
     * 接收物料批次
     * @param materialName 物料
     * @param warehouseRrn 仓库
     * @param qty 数量
     */
    static buildReceiveMaterialLot(materialName, warehouseRrn, qty) {
        let materialLot = new MaterialLot();
        materialLot.setMaterialName(materialName);

        let materialLotAction = new MaterialLotAction();
        materialLotAction.setTargetWarehouseRrn(warehouseRrn);
        materialLotAction.setTransQty(qty);
        return new MaterialLotManagerRequestBody(ActionType.Receive2Warehouse, materialLot, materialLotAction);
    }

}

