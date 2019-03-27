import EntityForm from './EntityForm';
import MaterialLotInvManagerRequest from '../../api/material-lot-inv-manager/MaterialLotInvManagerRequest';

export default class TransferMLotInventoryForm extends EntityForm {
    static displayName = 'TransferMLotInventoryForm';

    handleSave = () => {
        var self = this;
        let transferInvObject = this.props.object;
        let object = {
            transferInvObject : transferInvObject,
            success: function(responseBody) {
                if (self.props.onOk) {
                    self.props.onOk(responseBody.materialLotInventory);
                }
            }
            
        }
        MaterialLotInvManagerRequest.sendTransferStockRequest(object);
    }
}

 
