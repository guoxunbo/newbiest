import React from 'react';

import './ListTable.scss';
import EntityListTable from './EntityListTable';
import { Form, Button } from 'antd';
import IconUtils from '../../api/utils/IconUtils';
import BarCodeForm, { CodeType } from '../Form/BarCodeForm';
import MaterialLotActionForm from '../Form/MaterialLotActionForm';
import I18NUtils from '../../api/utils/I18NUtils';
import { i18NCode } from '../../api/const/i18n';
import TableManagerRequest from '../../api/table-manager/TableManagerRequest';
import TableObject from '../../api/dto/ui/Table';
import { ActionType } from '../../api/material-lot-manager/MaterialLotManagerRequestBody';

const TableName = {
    MLotConsumeAction: "MMLotComsume"
}
export default class MaterialLotTable extends EntityListTable {

    static displayName = 'MaterialLotTable';

    constructor(props) {
        super(props);
        let state = Object.assign(this.state, {
            materialLotActionTable: {fields:[]},
            showCodeType: "",
            okText: "",
            codeValue: ""
        });
        this.state = state;
    }
    
    createForm = () => {
        let children = [];
        const WrappedAdvancedBarCodeForm = Form.create()(BarCodeForm);
        children.push(<WrappedAdvancedBarCodeForm width={400} type={this.state.showCodeType} key={BarCodeForm.displayName} ref={this.formRef} value={this.state.codeValue} visible={this.state.barCodeFormVisible} 
                                                            okText={this.state.okText} onOk={this.handlePrintOk} onCancel={this.handleCancelPrint} />);                                   
        
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
        operations.push(this.buildBarCodeButton(record));
        operations.push(this.buildQrCodeButton(record));
        return operations;
    }

    handlePrintOk = () => {
        this.setState({
            barCodeFormVisible: false
        })
    }

    handleCancelPrint = () => {
        this.setState({
            barCodeFormVisible: false
        })
    }

    buildBarCodeButton = (record) => {
        return <Button key="barcode" style={{marginRight:'1px'}} onClick={() => this.handleShowBarCode(record)} size="small" href="javascript:;">
                     {IconUtils.buildIcon("icon-barcode")}
                </Button>;
    }

    buildQrCodeButton = (record) => {
        return <Button key="qrcode" style={{marginRight:'1px'}} onClick={() => this.handleShowQrCode(record)} size="small" href="javascript:;">
                     {IconUtils.buildIcon("icon-qrcodescan")}
                </Button>;
    }

    handleShowBarCode = (record) => {
        this.setState({
            barCodeFormVisible: true,
            codeValue: record.materialLotId,
            okText: I18NUtils.getClientMessage(i18NCode.BtnPrint),
            showCodeType: CodeType.BarCode
        })
    }

    handleShowQrCode = (record) => {
        this.setState({
            barCodeFormVisible: true,
            codeValue: JSON.stringify(record),
            okText: I18NUtils.getClientMessage(i18NCode.BtnDownload),
            materialLot: record,
            showCodeType: CodeType.QrCode
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