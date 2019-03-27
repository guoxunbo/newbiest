import MaterialLot from "../dto/mms/MaterialLot";
import MaterialLotAction from "../dto/mms/MaterialLotAction";

const ActionType = {
    StockOut: "StockOut",
    Transfer: "Transfer",
    Pick: "Pick",
    Check: "Check"
}

export default class MaterialLotInvManagerRequestBody {

    actionType;
    material;

    constructor(actionType, materialLot, materialLotAction){
        this.actionType = actionType;
        this.materialLot = materialLot;
        this.materialLotAction = materialLotAction;
    }

    /**
     * 物料批次出库
     * 扣除库存以及物料批次数量
     * @param materialLotInventory 库存
     */
    static buildStockOut(materialLotInventory) {
        let materialLot = new MaterialLot();
        materialLot.setObjectRrn(materialLotInventory.materialLotRrn);
        materialLot.setMaterialLotId(materialLotInventory.materialLotId);

        let materialLotAction = new MaterialLotAction();
        materialLotAction.setFromWarehouseRrn(materialLotInventory.warehouseRrn);
        materialLotAction.setTransQty(materialLotInventory.stockQty);
        return new MaterialLotInvManagerRequestBody(ActionType.StockOut, materialLot, materialLotAction);
    }

    /**
     * 物料批次转库
     * @param transferInvObject 在库对象。包含了目标仓库
     */
    static buildTransferInv(transferInvObject) {
        let materialLot = new MaterialLot();
        materialLot.setMaterialLotId(transferInvObject.materialLotId);

        let materialLotAction = new MaterialLotAction();
        materialLotAction.setFromWarehouseRrn(transferInvObject.warehouseRrn);
        materialLotAction.setTargetWarehouseRrn(transferInvObject.targetWarehouseRrn);
        materialLotAction.setTransQty(transferInvObject.stockQty);
        return new MaterialLotInvManagerRequestBody(ActionType.Transfer, materialLot, materialLotAction);
    }

    /**
     * 领料
     * @param transferInvObject 在库对象。包含了目标仓库
     */
    static buildPick(materialLotInventory) {
        let materialLot = new MaterialLot();
        materialLot.setObjectRrn(materialLotInventory.materialLotRrn);
        materialLot.setMaterialLotId(materialLotInventory.materialLotId);

        let materialLotAction = new MaterialLotAction();
        materialLotAction.setFromWarehouseRrn(materialLotInventory.warehouseRrn);
        materialLotAction.setTransQty(materialLotInventory.stockQty);
        return new MaterialLotInvManagerRequestBody(ActionType.Pick, materialLot, materialLotAction);
    }

    /**
     * 盘点
     * @param materialLotInventory 在库对象。包含了目标仓库
     */
    static buildCheck(checkInvObject) {
        let materialLot = new MaterialLot();
        materialLot.setObjectRrn(checkInvObject.materialLotRrn);
        materialLot.setMaterialLotId(checkInvObject.materialLotId);

        let materialLotAction = new MaterialLotAction();
        materialLotAction.setFromWarehouseRrn(checkInvObject.warehouseRrn);
        materialLotAction.setTransQty(checkInvObject.currentQty);
        materialLotAction.setActionCode(checkInvObject.actionCode);
        materialLotAction.setActionReason(checkInvObject.actionReason);
        materialLotAction.setActionComment(checkInvObject.actionComment);

        return new MaterialLotInvManagerRequestBody(ActionType.Check, materialLot, materialLotAction);
    }
}

