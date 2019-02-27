import EntityManagerRequest from '../../api/entity-manager/EntityManagerRequest';
import EntityForm from './EntityForm';
import RawMaterialManagerRequest from '../../api/raw-material-manager/RawMaterialManagerRequest';

export default class MaterialForm extends EntityForm {
    static displayName = 'MaterialForm';

    handleSave = () => {
        var self = this;
        // 默认处理的saveEntity
        let object = {
            rawMaterial: this.props.object,
            success: function(responseBody) {
                if (self.props.onOk) {
                    self.props.onOk(responseBody.material);
                }
            }
        };
        RawMaterialManagerRequest.sendMergeRequest(object);
    }
}


