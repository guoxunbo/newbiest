import {Button, Form} from 'antd';
import { i18NCode } from "../../api/const/i18n";
import I18NUtils from "../../api/utils/I18NUtils";
import RoleManagerRequest from "../../api/role-manager/RoleManagerRequest";
import { Notification } from "../notice/Notice";
import EntityForm from "../Form/EntityForm";
import DispatchUserForm from "../Form/DispatchUserForm";
import DispatchAuthorityForm from "../Form/DispatchAuthorityForm";
import EntityListTable from "./EntityListTable";

export default class RoleTable extends EntityListTable {

    static displayName = 'RoleTable';

    createForm = () => {
        let childrens = [];

        const WrappedAdvancedEntityForm = Form.create()(EntityForm);
        childrens.push(<WrappedAdvancedEntityForm key="EntityForm" ref={this.formRef} object={this.state.editorObject} visible={this.state.formVisible} 
                                            table={this.state.table} onOk={this.refresh} onCancel={this.handleCancel} />);
        
        const WrappedAdvancedDispatchUserForm = Form.create()(DispatchUserForm);      
        childrens.push(<WrappedAdvancedDispatchUserForm key="DispatchUserForm" 
                            ref={this.formRef} visible={this.state.dispatchUserFormVisible} 
                            dataSource={this.state.allUsers} targetKeys={this.state.roleUsers}
                            onOk={this.handleDispatchUser} onCancel={this.cancelDispatchUser} />);
        
        const WrappedAdvancedDispatchAuthorityForm = Form.create()(DispatchAuthorityForm);      
        childrens.push(<WrappedAdvancedDispatchAuthorityForm key="DispatchAuthorityForm" 
                            ref={this.formRef} visible={this.state.dispatchAuthorityFormVisible} 
                            authorities={this.state.allAuthorities} checkedKeys={this.state.checkedAuthorities}
                            roleAuthorities={this.state.roleAuthorities}
                            onOk={this.hanleDispatchAuthority} onCancel={this.cancelDispatchAuthority} />);
                            
        return childrens;                
    }

    hanleDispatchAuthority = (targetKeys) => {
        const {selectedRows} = this.state;
        let self = this;
        let object = {
            roleRrn: selectedRows[0].objectRrn,
            authorityKeys: targetKeys,
            success: function(responseBody) {
                self.setState({
                    dispatchAuthorityFormVisible: false
                });
                self.refresh(responseBody.role);
            }
        };
        RoleManagerRequest.sendDispatchAuthorityRequest(object);
    } 

    handleDispatchUser = (targetKeys) => {
        const {selectedRows} = this.state;
        let self = this;
        let object = {
            roleRrn: selectedRows[0].objectRrn,
            userKeys: targetKeys,
            success: function(responseBody) {
                self.setState({
                    dispatchUserFormVisible: false
                });
                self.refresh(responseBody.role);
            }
        };
        RoleManagerRequest.sendDispatchUserRequest(object);
    }

    cancelDispatchUser = () => {
        this.setState({
            dispatchUserFormVisible: false
        })
    }

    openDispatchUserForm = () => {
        let self = this;
        let selectedRow = this.validateSelectedRow();
        if (selectedRow) {
            let object = {
                roleRrn: selectedRow.objectRrn,
                success: function(roleResponseBody, entityListResponseBody) {
                    let allUsers = [];
                    let roleUserKeys = [];

                    let users = entityListResponseBody.dataList;
                    let role = roleResponseBody.role;
                    if (users && Array.isArray(users)) {
                        // 全部用户转换成transferItem
                        users.forEach((user) => {
                            let transferItem = DispatchUserForm.buildTransferItem(user.objectRrn, user.username, user.description);
                            allUsers.push(transferItem);
                        });
                        // 转换role已经存在用户
                        if (role.users && Array.isArray(role.users)) {
                            role.users.forEach((user) => {
                                roleUserKeys.push(user.objectRrn);
                            });
                        }
                    }
                    self.setState({
                        dispatchUserFormVisible: true,
                        allUsers: allUsers,
                        roleUsers: roleUserKeys
                    })
                }
            }
            RoleManagerRequest.sendGetRoleByRrnAndGetAllUserRequest(object);
        }
    }

    openDispatchAuthorityForm = () => {
        let self = this;
        let selectedRow = this.validateSelectedRow();
        if (selectedRow) {
            let object = {
                roleRrn: selectedRow.objectRrn,
                success: function(roleResponseBody, authorityResponseBody) {
                    let role = roleResponseBody.role;
                    let allAuthorities = authorityResponseBody.authorities;

                    let roleAuthorities = [];
                    let checkdKeys = [];
                    if (role.authorities && Array.isArray(role.authorities)) {
                        role.authorities.forEach((authority) => {
                            // 父级菜单永远不选中，依赖antd的tree特性当子级所有都有选中的时候，就会自动把父级节点选中
                            if (authority.subAuthorities == undefined || authority.subAuthorities.length == 0) {
                                checkdKeys.push(authority.objectRrn);
                            }
                            roleAuthorities.push(authority.objectRrn);
                        });

                    }

                    self.setState({
                        allAuthorities: allAuthorities,
                        roleAuthorities: roleAuthorities,
                        checkedAuthorities: checkdKeys,
                        dispatchAuthorityFormVisible: true,
                    })
                }
            }
            RoleManagerRequest.sendGetRoleByRrnAndGetTreeAuthorityRequest(object);
        }
    }
    
    cancelDispatchAuthority = () => {
        this.setState({
            dispatchAuthorityFormVisible: false
        })
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

    createButtonGroup = () => {
        let buttons = [];
        buttons.push(this.createAddButton());
        buttons.push(this.createImportButton());
        buttons.push(this.createExportDataAndTemplateButton());
        buttons.push(<Button key="dispatchUser" type="primary" style={styles.tableButton} icon="user-add" onClick={() => this.openDispatchUserForm()}>{I18NUtils.getClientMessage(i18NCode.BtnDispatchUser)}</Button>);
        buttons.push(<Button key="dispatchAutority" type="primary" style={styles.tableButton} icon="audit" onClick={() => this.openDispatchAuthorityForm()}>{I18NUtils.getClientMessage(i18NCode.BtnDispatchAuthority)}</Button>);
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