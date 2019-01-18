import {Component} from "react";
import I18NUtils from "../../api/utils/I18NUtils";
import { i18NCode } from "../../api/const/i18n";
import { Tree, Modal } from "antd";
import { Application } from "../../api/Application";
import IconUtils from "../../api/utils/IconUtils";

const { TreeNode } = Tree;

export default class DispatchAuthorityForm extends Component {

    static displayName = 'DispatchAuthorityForm';

    constructor(props) {
      super(props);
      const{authorities, checkedKeys, roleAuthorities} = this.props;

      this.state = {
        authorities: authorities,
        checkedKeys: checkedKeys,
        roleAuthorities: roleAuthorities,
      }

    }

    /**
     * 因为存在halfCheck。即选中子节点表示选中父节点。
     * antd默认的checkedKeys不会有半选中状态。需要自己获取并传递
     */
    onCheck = (checkedKeys, info) => {
      let roleAuthorities = [...checkedKeys, ...info.halfCheckedKeys]
      this.setState({ checkedKeys, roleAuthorities});
    }

    renderTreeNodes = (authorities) => {
      if (authorities) {
        let treeNodes = [];
        authorities.map((authority) => {
          treeNodes.push(<TreeNode icon={IconUtils.buildIcon(authority.image)} title={authority.labelZh} key={authority.objectRrn} dataRef={authority}>
                             {this.renderTreeNodes(authority.subAuthorities)}
                           </TreeNode>);
        });
        return treeNodes;
      }
    }

    buildForm = () => {
        return (
            <Tree
            showIcon
            checkable
            onCheck={this.onCheck}
            checkedKeys={this.state.checkedKeys}
            >
            {this.renderTreeNodes(this.props.authorities)}
            </Tree>
        );
    }

    handleOk = () => {
      const {roleAuthorities} = this.state;
      if (this.props.onOk) {
        this.props.onOk(roleAuthorities);
      }
    }

    render() {
        return (
            <div>
                <Modal width={Application.dispatchForm.modalWidth} centered title={I18NUtils.getClientMessage(i18NCode.Edit)} object={this.props.object} visible={this.props.visible} 
                    maskClosable={false} onOk={this.handleOk} onCancel={this.props.onCancel} 
                    okText={I18NUtils.getClientMessage(i18NCode.Ok)} 
                    cancelText={I18NUtils.getClientMessage(i18NCode.Cancel)}>
                    {this.buildForm()}
                </Modal>
            </div>
        );
    }

}