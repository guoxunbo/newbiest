export default class MaterialLotAction {
    
    transQty;
    fromWarehouseRrn;
    targetWarehouseRrn;

    setTransQty(transQty) {
        this.transQty = transQty;
    }
    
    setFromWarehouseRrn(fromWarehouseRrn) {
        this.fromWarehouseRrn = fromWarehouseRrn;
    }

    setTargetWarehouseRrn(targetWarehouseRrn) {
        this.targetWarehouseRrn = targetWarehouseRrn;
    }
    
}