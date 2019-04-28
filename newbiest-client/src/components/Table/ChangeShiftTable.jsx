import EntityListTable from './EntityListTable';
import ChangeShiftForm from '../Form/ChangeShiftForm';
import { Form, Button } from 'antd';
import I18NUtils from '../../api/utils/I18NUtils';
import { i18NCode } from '../../api/const/i18n';
import ChangeShiftRequest from '../../api/change-shift-manager/ChangeShiftRequest';
import EntityManagerRequest from '../../api/entity-manager/EntityManagerRequest';

export default class ChangeShiftTable extends EntityListTable {

    static displayName = 'ChangeShiftTable';

    createButtonGroup = () => {
        let buttons = [];
        buttons.push(this.createAddButton());
        buttons.push(this.createOpenButton());
        buttons.push(this.createCloseButton());
        buttons.push(this.createExportDataButton());
        return buttons;
    }

    createOpenButton = () => {
        return <Button key="open" type="primary" style={styles.tableButton} icon="folder-open" onClick={() => this.handleOpen()}>{I18NUtils.getClientMessage(i18NCode.BtnOpen)}</Button>;
    }

    createCloseButton = () => {
        return <Button key="close" type="primary" style={styles.tableButton} icon="close-circle" onClick={() => this.handleClose()}>{I18NUtils.getClientMessage(i18NCode.BtnClose)}</Button>;
    }

    handleDelete = (record) => {
        const self = this;
        let object = {
            modelClass : self.state.table.modelClass,
            values: record,
            deleteRelationEntityFlag: true,
            success: function(responseBody) {
                self.refreshDelete(record);
            }
        };
        EntityManagerRequest.sendDeleteRequest(object);
    } 

    handleOpen = () => {
        let self = this;
        let selectedRow = this.getSingleSelectedRow();
        if (selectedRow) {
            let object = {
                changeShift: selectedRow,
                success: function(responseBody) {
                    self.refresh(responseBody.changeShift)
                }
            }
            ChangeShiftRequest.sendOpenRquest(object);
        }
    }

    handleClose = () => {
        let self = this;
        let selectedRow = this.getSingleSelectedRow();
        if (selectedRow) {
            let object = {
                changeShift: selectedRow,
                success: function(responseBody) {
                    self.refresh(responseBody.changeShift)
                }
            }
            ChangeShiftRequest.sendCloseRquest(object);
        }
    }

    createForm = () => {
        const WrappedAdvancedEntityForm = Form.create()(ChangeShiftForm);
        return  <WrappedAdvancedEntityForm ref={this.formRef} object={this.state.editorObject} visible={this.state.formVisible} 
                                            table={this.state.table} onOk={this.refresh} onCancel={this.handleCancel} />
    }
}
const styles = {
    tableButton: {
        marginLeft:'20px'
    },
};
