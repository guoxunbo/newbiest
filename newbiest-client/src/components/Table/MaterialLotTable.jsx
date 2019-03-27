import React from 'react';

import './ListTable.scss';
import EntityListTable from './EntityListTable';
import { Form, Button } from 'antd';
import IconUtils from '../../api/utils/IconUtils';
import BarCodeForm from '../Form/BarCodeForm';

export default class MaterialLotTable extends EntityListTable {

    static displayName = 'MaterialLotTable';

    constructor(props) {
        super(props);
        let state = Object.assign(this.state, {
            materialLot: {},
        });
        this.state = state;
    }
    
    createForm = () => {
        let children = [];
        const WrappedAdvancedTransferMLotInventoryForm = Form.create()(BarCodeForm);
        children.push(<WrappedAdvancedTransferMLotInventoryForm key={BarCodeForm.displayName} ref={this.formRef} value={this.state.materialLot.materialLotId} visible={this.state.barCodeFormVisible} 
                                                             onOk={this.handlePrintOk} onCancel={this.handleCancelPrint} />);                                   
        return children;
    }
    /**
     * 创建btn组。不同的table对button的组合要求不一样时。可以重载其方法做处理
     */
    createButtonGroup = () => {
        let buttons = [];
        buttons.push(this.createExportDataButton());
        return buttons;
    }

    /**
     * 物料批次不可更新和删除
     */
    buildOperation = (record) => {
        let operations = [];
        operations.push(this.buildPrintButton(record));
        return operations;
    }

    handlePrintOk = (record) => {
        //TODO 此处要做打印的 或者调用Print标签
        this.setState({
            barCodeFormVisible: false
        })
    }

    handleCancelPrint = () => {
        this.setState({
            barCodeFormVisible: false
        })
    }

    buildPrintButton = (record) => {
        return <Button key="print" style={{marginRight:'1px'}} onClick={() => this.handlePrint(record)} size="small" href="javascript:;">
                     {IconUtils.buildIcon("icon-dayin")}
                </Button>;
    }

    handlePrint = (record) => {
        this.setState({
            barCodeFormVisible: true,
            materialLot: record
        })
    }


}