import React, { Component } from 'react';
import { Table, Popconfirm, Button,Form } from 'antd';
import './ListTable.scss';
import {Application} from '../../api/Application'
import {DefaultRowKey, UrlConstant} from '../../api/const/ConstDefine'
import TableManagerRequestBody from '../../api/table-manager/TableManagerRequestBody';
import TableManagerRequestHeader from '../../api/table-manager/TableManagerRequestHeader';
import Request from '../../api/Request';
import MessageUtils from '../../api/utils/MessageUtils';
import Field from '../../api/dto/ui/Field';
import EntityForm from '../Form/EntityForm';
import * as PropTypes from 'prop-types';
import EntityManagerRequestBody from '../../api/entity-manager/EntityManagerRequestBody';
import EntityManagerRequestHeader from '../../api/entity-manager/EntityManagerRequestHeader';
import TableObject from '../../api/dto/ui/Table';
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
            data: [],
        };
    }

    componentWillReceiveProps = (props) => {
        this.setState({
            data: props.data
        })
    }

    componentWillMount = () => {
        this.setState({
            rowClassName: (record, index) => this.getRowClassName(record, index),
        });
    }

    componentDidMount() {
        this.buildTable(this.props.tableRrn);
    }
    
    getRowClassName = (record, index) => {
        if(index % 2 ===0) {
            return 'even-row'; 
        } else {
            return ''; 
        }
    };

    buildTable = (tableRrn) => {
        const self = this;
        let requestBody = TableManagerRequestBody.buildGetByRrn(tableRrn);
        let requestHeader = new TableManagerRequestHeader();
        let request = new Request(requestHeader, requestBody, UrlConstant.TableMangerUrl);
        let requestObject = {
            request: request,
            success: function(responseBody) {
                let table = responseBody.table;
                let columnData = self.buildColumn(table.fields);
                self.setState({
                    table: responseBody.table,
                    columns: columnData.columns,
                    scrollX: columnData.scrollX
                });
            }
        }
        MessageUtils.sendRequest(requestObject);
    }

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
        let oprationColumn = this.buildOprationColumn();
        scrollX += oprationColumn.width;
        columns.push(oprationColumn);
        return {
            columns: columns,
            scrollX: scrollX
        };
    }

    buildOprationColumn() {
        let self = this;
        let oprationColumn = {
            key: "opration",
            title: "opration",
            dataIndex: "opration",
            align: "center",
            fixed: 'right',
            width: Application.table.oprationColumn.width,
            render: (text, record) => {
                return (
                    <div>
                        <Button style={{marginRight:'1px'}} icon="eye" onClick={() => self.handleEdit(record)} size="small" href="javascript:;"></Button>
                        <Button style={{marginRight:'1px'}} icon="form" onClick={() => self.handleEdit(record)} size="small" href="javascript:;"></Button>
                        <Popconfirm title="Sure to delete?" onConfirm={() => self.handleDelete(record)}>
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
        let requestBody = EntityManagerRequestBody.buildDeleteEntity(this.state.table.modelClass, record, false);
        let requestHeader = new EntityManagerRequestHeader();
        let request = new Request(requestHeader, requestBody, UrlConstant.EntityManagerUrl);
        let requestObject = {
            request: request,
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
        }
        MessageUtils.sendRequest(requestObject);
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

    handleSave = (e) => {
        const form = this.form;
        form.validateFields((err, values) => {
            if (err) {
                return;
            }
            var self = this;
            //TODO 当有1对多的情况。需要考虑是否更新还是多的保持原状。
            let requestBody = EntityManagerRequestBody.buildMergeEntity(this.state.table.modelClass, values);
            let requestHeader = new EntityManagerRequestHeader();
            let request = new Request(requestHeader, requestBody, UrlConstant.EntityManagerUrl);
            let requestObject = {
                request: request,
                success: function(responseBody) {
                    let responseData = responseBody.data;
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
            }
            MessageUtils.sendRequest(requestObject);
            
        });
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
        return <Button style={styles.tableButton} type="primary" icon="plus" onClick={() => this.handleAdd()}>新增</Button>;
    }

    render() {
        const {data, columns, rowClassName, rowSelection, scrollX} = this.state;
        const WrappedAdvancedEntityForm = Form.create()(EntityForm);
        return (
          <div >
            {this.createButtonGroup()}
            <div style={styles.tableContainer}>
                <Table
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
                >
                </Table>
                <WrappedAdvancedEntityForm ref={this.formRef} object={this.state.editorObject} visible={this.state.formVisible} fields={this.state.table.fields}
                    onOk={this.handleSave} onCancel={this.handleCancel} tabs={this.state.table.tabs}/>
            </div>
          </div>
        );
    }
}

EntityListTable.prototypes = {
    tableRrn: PropTypes.number.isRequired,
    data: PropTypes.array,
    rowClassName: PropTypes.func,
    rowkey: PropTypes.string,
    pagination: PropTypes.pagination
}

const styles = {
    tableButton: {
        position:'absolute',
        top:'120px',
        right:'120px'
    }
};