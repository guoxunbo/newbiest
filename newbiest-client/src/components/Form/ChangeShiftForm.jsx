import EntityForm from './EntityForm';
import EntityManagerRequest from '../../api/entity-manager/EntityManagerRequest';
import ChangeShiftRequest from '../../api/change-shift-manager/ChangeShiftRequest';
export default class ChangeShiftForm extends EntityForm {
    static displayName = 'ChangeShiftForm';

    handleSave = (formObject) => {
        var self = this;
        // 默认处理的saveEntity
        let object = {
            values: formObject,
            success: function(responseBody) {
                if (self.props.onOk) {
                    self.props.onOk(responseBody.changeShift);
                }
            }
        };
        ChangeShiftRequest.sendMergeRequest(object);
    }
}


