import EntityForm from './EntityForm';
import MaterialLotManagerRequest from '../../api/material-lot-manager/MaterialLotManagerRequest';

export default class ReceiveMaterialForm extends EntityForm {
    static displayName = 'ReceiveMaterialForm';

    handleSave = () => {
        var self = this;
        let object = {
            formObject: this.props.object,
            success: function(responseBody) {
                if (self.props.onOk) {
                    self.props.onOk(responseBody.material);
                }
            }
        };
        MaterialLotManagerRequest.sendReceiveMaterialLotRequest(object);
    }
}


