import  React, { Component } from 'react';

import { Table, Popconfirm, Button,Form } from 'antd';
import './ListTable.scss';
import {Application} from '../../api/Application'
import {DefaultRowKey} from '../../api/const/ConstDefine'
import MessageUtils from '../../api/utils/MessageUtils';
import Field from '../../api/dto/ui/Field';
import EntityForm from '../Form/EntityForm';
import * as PropTypes from 'prop-types';
import TableObject from '../../api/dto/ui/Table';
import $ from 'jquery';
import EntityManagerRequest from '../../api/entity-manager/EntityManagerRequest';
import I18NUtils from '../../api/utils/I18NUtils';
import { i18NCode } from '../../api/const/i18n';

/**
 * 基本表格。每一行都带有编辑和删除的列
 */
export default class EntityListTable extends Component {

    static displayName = 'EntityListTable';

    constructor(props) {
        super(props);
        this.state = {
            table: {fields: []},
            columns: [],
            rowClassName: (record, index) => {},
            rowSelection: undefined,
            selectedRowKeys: [],
            selectedRows: [],
            formVisible: false,
            editorObject: {},
            scrollX: undefined,
            scrollY:undefined,
            beforescrollX:undefined,
            data: [],
        };
    }

    componentWillReceiveProps = (props) => {
        let columnData = this.buildColumn(props.table.fields);
        this.setState({
            data: props.data,
            table: props.table,
            columns: columnData.columns,
            scrollX: columnData.scrollX
        })
    }

    componentWillMount = () => {
        this.setState({
            rowClassName: (record, index) => this.getRowClassName(record, index),
        });
    }

    componentDidMount() {
        $(window).bind('resize',() => {
            let maxWidth = document.querySelector('.custom-table').clientWidth
            this.setState({maxWidth})
        })
    }

    componentWillUnmount() {
        $(window).unbind('resize',function() {
        })
    }
    
    getRowClassName = (record, index) => {
        const {selectedRows} = this.state;
        if (selectedRows.indexOf(record) >= 0) {
            return 'selected-row';
        } else {
            if(index % 2 ===0) {
                return 'even-row'; 
            } else {
                return ''; 
            }
        }
        
    };

    buildColumn = (fields) => {
        let columns = [];
        let scrollX = 0;
        for (let field of fields) {
            let f  = new Field(field);
            let column = f.buildColumn();
            if (column != null) {
                columns.push(column);
                scrollX += column.width;
            }
        }
        this.setState({beforescrollX:scrollX})
        let operationColumn = this.buildOperationColumn();
        scrollX += operationColumn.width;
        columns.push(operationColumn);
        return {
            columns: columns,
            scrollX: scrollX
        };
    }

    buildOperationColumn() {
        let maxWidth = this.state.maxWidth ? this.state.maxWidth : document.querySelector('.custom-table').clientWidth;
        let self = this;
        
        let oprationColumn = {
            key: "opration",
            title: "opration",
            dataIndex: "opration",
            align: "center",
            fixed:maxWidth > this.state.beforescrollX + Application.table.oprationColumn.width ? false : 'right',
            width: Application.table.oprationColumn.width,
            render: (text, record) => {
                return (
                    <div>
                        <Button style={{marginRight:'1px'}} icon="edit" onClick={() => self.handleEdit(record)} size="small" href="javascript:;"></Button>
                        <Popconfirm title={I18NUtils.getClientMessage(i18NCode.ConfirmDelete)} onConfirm={() => self.handleDelete(record)}>
                            <Button icon="delete" size="small" type="danger"></Button>
                        </Popconfirm>
                    </div>
                );
            }
        };
        return oprationColumn;
    }

    handleDelete = (record) => {
        const self = this;
        let object = {
            modelClass : self.state.table.modelClass,
            values: record,
            success: function(responseBody) {
                let datas = self.state.data;
                let dataIndex = datas.indexOf(record);
                if (dataIndex > -1 ) {
                    datas.splice(dataIndex, 1);
                    self.setState({
                        data: datas
                    })
                }
                MessageUtils.showOperationSuccess();
            }
        };
        EntityManagerRequest.sendDeleteRequest(object);
    } 

    handleEdit = (record) => {
        this.setState({
            formVisible : true,
            editorObject: record
        })
    }

    handleAdd = () => {
        this.setState({
            formVisible : true,
            editorObject: TableObject.buildDefaultModel(this.state.table.fields)
        })
    }

    refresh = (responseData) => {
        var self = this;
        //TODO 当有1对多的情况。需要考虑是否更新还是多的保持原状。
        let datas = self.state.data;
        let dataIndex = -1;
        datas.map((data, index) => {
            if (data.objectRrn == responseData.objectRrn) {
                dataIndex = index;
            }
        });
        if (dataIndex > -1) {
            datas.splice(dataIndex, 1, responseData);
        } else {
            // 新增的就放在第一位
            datas.unshift(responseData);
        }
        self.setState({
            data: datas,
            formVisible: false
        }) 
        MessageUtils.showOperationSuccess();
    }

    handleCancel = (e) => {
        this.setState({
            formVisible: false
        })
    }

    formRef = (form) => {
        this.form = form;
    };

    /**
     * 创建btn组。不同的table对button的组合要求不一样时。可以重载其方法做处理
     */
    createButtonGroup = () => {
        let buttons = [];
        buttons.push(<Button key="add" type="primary" style={styles.tableButton} icon="plus" onClick={() => this.handleAdd()}>{I18NUtils.getClientMessage(i18NCode.BtnAdd)}</Button>);
        buttons.push(<Button key="import" type="primary" style={styles.tableButton} icon="file-add" onClick={() => this.handleAdd()}>{I18NUtils.getClientMessage(i18NCode.BtnImp)}</Button>);
        buttons.push(<Button key="export" type="primary" style={styles.tableButton} icon="export" onClick={() => this.handleAdd()}>{I18NUtils.getClientMessage(i18NCode.BtnExp)}</Button>);
        return buttons;
    }

    createForm = () => {
        const WrappedAdvancedEntityForm = Form.create()(EntityForm);
        return  <WrappedAdvancedEntityForm ref={this.formRef} object={this.state.editorObject} visible={this.state.formVisible} 
                                            table={this.state.table} onOk={this.refresh} onCancel={this.handleCancel} />
    }
    
    /**
     * 行点击事件
     */
    selectRow = (record) => {
        let selectedRows = [];
        selectedRows.push(record);
        this.setState({
            selectedRows: selectedRows
        });
    }

    /**
     * 默认的table框的选择框属性 此处不实现。
     */
    getRowSelection = (selectedRowKeys) => {

    }

    render() {
        const {data, columns, rowClassName, selectedRowKeys, scrollX} = this.state;
        const rowSelection = this.getRowSelection(selectedRowKeys);
        return (
          <div >
            <div style={styles.buttonGroup}>
                {this.createButtonGroup()}
            </div>
            <div style={styles.tableContainer}>
                <Table
                    ref= {el => this.table = el}
                    dataSource={data}
                    bordered
                    className="custom-table"
                    pagination={this.props.pagination == null ? Application.table.pagination : this.props.pagination}
                    columns = {columns}
                    scroll = {{ x: scrollX, y: 350 }}
                    rowKey = {this.props.rowkey == null ? DefaultRowKey : this.props.rowkey}
                    loading = {this.props.loading}
                    rowClassName = {rowClassName.bind(this)}
                    rowSelection = {rowSelection}
                    onRow={(record) => ({
                        onClick: () => {
                            this.selectRow(record);
                        },
                    })}
                >
                </Table>
            </div>
            {this.createForm()}
          </div>
        );
    }
}

EntityListTable.prototypes = {
    table: PropTypes.object.isRequired,
    data: PropTypes.array,
    rowClassName: PropTypes.func,
    rowkey: PropTypes.string,
    pagination: PropTypes.pagination
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