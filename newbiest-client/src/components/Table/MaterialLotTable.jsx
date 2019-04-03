import React from 'react';

import './ListTable.scss';
import EntityListTable from './EntityListTable';
import { Form, Button } from 'antd';
import IconUtils from '../../api/utils/IconUtils';
import BarCodeForm from '../Form/BarCodeForm';
import MaterialLotActionForm from '../Form/MaterialLotActionForm';
import I18NUtils from '../../api/utils/I18NUtils';
import { i18NCode } from '../../api/const/i18n';
import TableManagerRequest from '../../api/table-manager/TableManagerRequest';
import TableObject from '../../api/dto/ui/Table';
import { ActionType } from '../../api/material-lot-manager/MaterialLotManagerRequestBody';
import MessageUtils from '../../api/utils/MessageUtils';

const TableName = {
    MLotConsumeAction: "MMLotComsume"
}
export default class MaterialLotTable extends EntityListTable {

    static displayName = 'MaterialLotTable';

    constructor(props) {
        super(props);
        let state = Object.assign(this.state, {
            materialLot: {},
            materialLotActionTable: {fields:[]}
        });
        this.state = state;
    }
    
    createForm = () => {
        let children = [];
        const WrappedAdvancedTransferMLotInventoryForm = Form.create()(BarCodeForm);
        children.push(<WrappedAdvancedTransferMLotInventoryForm key={BarCodeForm.displayName} ref={this.formRef} value={this.state.materialLot.materialLotId} visible={this.state.barCodeFormVisible} 
                                                            onOk={this.handlePrintOk} onCancel={this.handleCancelPrint} />);                                   
        const WrappedAdvancedMaterialActionForm = Form.create()(MaterialLotActionForm);
        children.push(<WrappedAdvancedMaterialActionForm key={MaterialLotActionForm.displayName} ref={this.formRef} object={this.state.materialLotAction} visible={this.state.materialLotActionVisible} 
                        action={this.state.action} table={this.state.materialLotActionTable} onOk={this.handleActionOk} onCancel={this.handleCancelAction} />);                                   
                                                         
        return children;
    }
    /**
     * 创建btn组。不同的table对button的组合要求不一样时。可以重载其方法做处理
     */
    createButtonGroup = () => {
        let buttons = [];
        buttons.push(this.createConsumeButton());
        buttons.push(this.createExportDataButton());
        return buttons;
    }

    createConsumeButton = () => {
        return <Button key="consume" type="primary" style={styles.tableButton} onClick={() => this.handleAction(ActionType.Consume, TableName.MLotConsumeAction)}>
                        {IconUtils.buildIcon("icon-consume")}  {I18NUtils.getClientMessage(i18NCode.BtnConsume)}
                    </Button>
    }

    handleAction = (action, tableName) => {
        const selectedObject = this.getSingleSelectedRow();
        if (!selectedObject) {
            return;
        }
        let self = this;
        let requestObject = {
            name: tableName,
            success: function(responseBody) {
                let table = responseBody.table;
                let materialLotAction = TableObject.buildDefaultModel(table.fields, selectedObject);
                materialLotAction.materialLot = selectedObject;
                self.setState({
                    materialLotAction: materialLotAction,
                    materialLotActionTable: responseBody.table,
                    materialLotActionVisible : true,
                    action: action
                });
            }
        }
        TableManagerRequest.sendGetByNameRequest(requestObject);
    }

    handleActionOk =(materialLot) => {
        this.setState({
            materialLotActionVisible: false
        });
        this.refresh(materialLot);
    }

    handleCancelAction = () => {
        this.setState({
            materialLotActionVisible: false
        })
    }
    
    /**
     * 物料批次不可更新和删除
     */
    buildOperation = (record) => {
        let operations = [];
        operations.push(this.buildPrintButton(record));
        return operations;
    }

    handlePrintOk = () => {
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