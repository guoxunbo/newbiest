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
        const self = this;
        if (selectedRows) {
            if (selectedRows.length != 1) {
                Notification.showNotice(I18NUtils.getClientMessage(i18NCode.SelectOneRow));
                return;
            } 
            let object = {
                username: selectedRows[0].username,
                success: function(responseBody) {
                    self.refresh(responseBody.user);
                }
            }
            UserManagerRequest.sendResetPassword(object);
        }
    }

    createButtonGroup() {
        let buttons = this.__proto__.createButtonGroup();
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
