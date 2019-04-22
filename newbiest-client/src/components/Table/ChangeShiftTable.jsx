import EntityListTable from './EntityListTable';
import ChangeShiftForm from '../Form/ChangeShiftForm';
import { Form } from 'antd';

export default class ChangeShiftTable extends EntityListTable {

    static displayName = 'ChangeShiftTable';

    createButtonGroup = () => {
        let buttons = [];
        buttons.push(this.createAddButton());
        buttons.push(this.createExportDataButton());
        return buttons;
    }

    createForm = () => {
        const WrappedAdvancedEntityForm = Form.create()(ChangeShiftForm);
        return  <WrappedAdvancedEntityForm ref={this.formRef} object={this.state.editorObject} visible={this.state.formVisible} 
                                            table={this.state.table} onOk={this.refresh} onCancel={this.handleCancel} />
    }
}
