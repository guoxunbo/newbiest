import {Button} from 'antd';
import UserManagerRequest from '../../api/user-manager/UserManagerRequest';
import MessageUtils from "../../api/utils/MessageUtils";
import { Notification } from "../notice/Notice";
import I18NUtils from "../../api/utils/I18NUtils";
import { i18NCode } from "../../api/const/i18n";
import UserForm from "../Form/UserForm";
import {Form} from 'antd';
import EntityListTable from "./EntityListTable";

export default class UserTable extends EntityListTable {

    static displayName = 'UserTable';

    constructor(props) {
        super(props);
    }

    createForm = () => {
        const WrappedAdvancedEntityForm = Form.create()(UserForm);
        return  <WrappedAdvancedEntityForm ref={this.formRef} object={this.state.editorObject} visible={this.state.formVisible} 
                                            table={this.state.table} onOk={this.refresh} onCancel={this.handleCancel} />
    }

    resetPassword = () => {
        const {selectedRows} = this.state;
        if (selectedRows) {
            if (selectedRows.length != 1) {
                Notification.showNotice(I18NUtils.getClientMessage(i18NCode.SelectOneRow));
                return;
            } 
            let object = {
                username: selectedRows[0].username,
                success: function(responseBody) {
                    MessageUtils.showOperationSuccess();
                }
            }
            UserManagerRequest.sendResetPassword(object);
        }
    }

    createButtonGroup = () => {
        // 当前未知super.createButtonGroup()这么调用会报错。未知原因。故此处copy代码
        let buttons = [];
        buttons.push(<Button key="add" type="primary" style={styles.tableButton} icon="plus" onClick={() => this.handleAdd()}>{I18NUtils.getClientMessage(i18NCode.BtnAdd)}</Button>);
        buttons.push(<Button key="import" type="primary" style={styles.tableButton} icon="file-add" onClick={() => this.handleAdd()}>{I18NUtils.getClientMessage(i18NCode.BtnImp)}</Button>);
        buttons.push(<Button key="export" type="primary" style={styles.tableButton} icon="export" onClick={() => this.handleAdd()}>{I18NUtils.getClientMessage(i18NCode.BtnExp)}</Button>);
        buttons.push(<Button key="resetPwd" type="primary" style={styles.tableButton} icon="lock" onClick={() => this.resetPassword()}>{I18NUtils.getClientMessage(i18NCode.BtnResetPassword)}</Button>);
        return buttons;
    }

}
const styles = {
    tableButton: {
        marginLeft:'20px'
    },
    buttonGroup:{
        marginBottom:'10px',
        marginRight:'30px',
        textAlign:'right'
    }
};
