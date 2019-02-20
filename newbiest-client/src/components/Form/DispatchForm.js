import {Component} from "react";
import I18NUtils from "../../api/utils/I18NUtils";
import { i18NCode } from "../../api/const/i18n";
import { Transfer, Modal } from "antd";
import * as PropTypes from 'prop-types';
import { Application } from "../../api/Application";

/**
 * DispatchForm
 * 具有左右互移功能
 */
export default class DispatchForm extends Component {

    static displayName = 'DispatchForm';

    constructor(props) {
        super(props);
        const{dataSource, targetKeys } = this.props;
        this.state={
            dataSource:dataSource,
            targetKeys:targetKeys
        }
    }

    /**
     * 创建transferItem对象。
     * 因为<Transfer>只能接受值为TransferItem类型的值 
     *  https://github.com/ant-design/ant-design/blob/1bf0bab2a7bc0a774119f501806e3e0e3a6ba283/components/transfer/index.tsx#L12
     * @param {主键比如objectRrn的值} key 
     * @param {显示信息} title 
     * @param {描述} description 
     */
    static buildTransferItem(key, title, description) {
        return {
            key: key, 
            title: title,
            description: description
        }
    }
    
    buildForm = () => {
        const{dataSource, targetKeys } = this.state;
        return (<Transfer showSearch 
            dataSource={dataSource} 
            targetKeys={targetKeys}
            listStyle={{
                width: Application.dispatchForm.width,
                height: Application.dispatchForm.height,
            }}
            locale={{
                itemUnit: I18NUtils.getClientMessage(i18NCode.TransferItem),
                itemsUnit: I18NUtils.getClientMessage(i18NCode.TransferItems),
                notFoundContent: I18NUtils.getClientMessage(i18NCode.TransferNotFoundContent),
                searchPlaceholder: I18NUtils.getClientMessage(i18NCode.TransferSearchPlaceholder),
            }}
            onChange={this.handleChange}
            titles={[I18NUtils.getClientMessage(i18NCode.TransferSource), I18NUtils.getClientMessage(i18NCode.TransferTarget)]}
            render={item => `${item.title}-${item.description}`}/>)

    }

    handleOk = () => {
        const {targetKeys} = this.state;
        if (this.props.onOk) {
            this.props.onOk(targetKeys);
        }
    }

    handleChange = (targetKeys) => {
        this.setState({ targetKeys });
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
DispatchForm.prototypes = {
    dataSource: PropTypes.array, // 所有数据
    targetKeys: PropTypes.array, // 已经选择数据
    onOk: PropTypes.func.isRequired,
    onCancel: PropTypes.func.isRequired,
}