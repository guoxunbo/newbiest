import EntityListTable from "./EntityListTable";
import { Button, Form } from "antd";
import { i18NCode } from "../../api/const/i18n";
import I18NUtils from "../../api/utils/I18NUtils";
import DispatchForm from "../Form/DispatchForm";
import EntityForm from "../Form/EntityForm";
import StatusModelRequest from "../../api/status-model-manager/StatusModelRequest";
import { Notification } from "../notice/Notice";

export default class StatusModelTable extends EntityListTable {

    static displayName = 'StatusModelTable';

    createForm = () => {
        let childrens = [];

        const WrappedAdvancedEntityForm = Form.create()(EntityForm);
        childrens.push(<WrappedAdvancedEntityForm key={EntityForm.displayName} ref={this.formRef} object={this.state.editorObject} visible={this.state.formVisible} 
                                            table={this.state.table} onOk={this.refresh} onCancel={this.handleCancel} />);
        
        const WrappedAdvancedDispatchUserForm = Form.create()(DispatchForm);      
        childrens.push(<WrappedAdvancedDispatchUserForm key="DispatchEventForm" 
                            ref={this.formRef} visible={this.state.dispatchFormVisible} 
                            dataSource={this.state.allEvents} targetKeys={this.state.modelEventKeys}
                            onOk={this.handleDispatchEvent} onCancel={this.cancelDispatchEvent} />);
        
        return childrens;                
    }

    openDispatchEventForm = () => {
        let self = this;
        let selectedRow = this.validateSelectedRow();
        const {eventModelClass} = this.props;

        if (selectedRow) {
            let object = {
                statusModelRrn: selectedRow.objectRrn,
                eventModelClass: eventModelClass,
                success: function(statusModelResponseBody, entityListResponseBody) {
                    let allEvents = [];
                    let modelEventKeys = [];
                    let events = entityListResponseBody.dataList;
                    let statusModel = statusModelResponseBody.statusModel;
                    if (events && Array.isArray(events)) {
                        // 全部事件转换成transferItem
                        events.forEach((event) => {
                            let transferItem = DispatchForm.buildTransferItem(event.objectRrn, event.name, event.description);
                            allEvents.push(transferItem);
                        });
                        // 转换statusModel已经存在的事件
                        if (statusModel.events && Array.isArray(statusModel.events)) {
                            statusModel.events.forEach((event) => {
                                modelEventKeys.push(event.objectRrn);
                            });
                        }
                    }
                    self.setState({
                        dispatchFormVisible: true,
                        allEvents: allEvents,
                        modelEventKeys: modelEventKeys
                    })
                }
            }
            StatusModelRequest.sendGetRoleByRrnAndGetAllEventRequest(object);
        }
    }

    handleDispatchEvent = (targetKeys) => {
        const {selectedRows} = this.state;
        let self = this;
        let object = {
            statusModelRrn: selectedRows[0].objectRrn,
            eventKeys: targetKeys,
            success: function(responseBody) {
                self.setState({
                    dispatchFormVisible: false
                });
                self.refresh(responseBody.statusModel);
            }
        };
        StatusModelRequest.sendDispatchEventRequest(object);
    }

    validateSelectedRow = () => {
        const {selectedRows} = this.state;
        if (selectedRows) {
            if (selectedRows.length != 1) {
                Notification.showNotice(I18NUtils.getClientMessage(i18NCode.SelectOneRow));
                return;
            } 
        }
        return selectedRows[0];
    }

    cancelDispatchEvent = () => {
        let self = this;
        self.setState({
            dispatchFormVisible: false,
        })
    }

    createButtonGroup = () => {
        let buttons = [];
        buttons.push(this.createAddButton());
        buttons.push(this.createImportButton());
        buttons.push(this.createExportDataAndTemplateButton());
        buttons.push(<Button key="dispatchEvent" type="primary" style={styles.tableButton} icon="user-add" onClick={() => this.openDispatchEventForm()}>{I18NUtils.getClientMessage(i18NCode.BtnDispatchEvent)}</Button>);
        return buttons;
    }
}

const styles = {
    tableButton: {
        marginLeft:'20px'
    }
};