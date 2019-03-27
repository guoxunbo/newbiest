import EntityForm from './EntityForm';
import MaterialLotInvManagerRequest from '../../api/material-lot-inv-manager/MaterialLotInvManagerRequest';

export default class CheckMLotInventoryForm extends EntityForm {
    static displayName = 'CheckMLotInventoryForm';

    handleSave = () => {
        var self = this;
        let checkInvObject = this.props.object;
        let object = {
            checkInvObject : checkInvObject,
            success: function(responseBody) {
                if (self.props.onOk) {
                    self.props.onOk(responseBody.materialLotInventory);
                }
            }
            
        }
        MaterialLotInvManagerRequest.sendCheckRequest(object);
    }
}


