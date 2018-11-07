import React, { Component } from 'react';
import { Table, Popconfirm, Button, Divider,Form } from 'antd';
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

/**
 * 基本表格。每一行都带有编辑和删除的列
 */
export default class EntityListTable extends Component {

    static displayName = 'EntityListTable';

    constructor(props) {
        super(props);
        this.state = {
            columns: [],
            rowClassName: (record, index) => {},
            rowSelection: undefined,
            selectedRowKeys: [],
            selectedRows: [],
            formVisible: false,
            fields: [],
            editorObject: undefined,
            scrollX: undefined
        };
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
                let fields = responseBody.table.fields;
                let columnData = self.buildColumn(fields);
                self.setState({
                    columns: columnData.columns,
                    fields: fields,
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
        if (this.props.check) {
            scrollX += 10;
        }
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
                        <Button  icon="form" onClick={() => self.handleEdit(record)} size="small" href="javascript:;">编辑</Button>
                        <Divider type="vertical" />
                        <Popconfirm title="Sure to delete?">
                            <Button icon="delete" size="small" type="danger">删除</Button>
                        </Popconfirm>
                    </div>
                );
            }
        };
        return oprationColumn;
    }

    handleEdit(record) {
        this.setState({
            formVisible : true,
            editorObject: record
        })
        console.log(record);
    }

    handleSave = (e) => {
        const form = this.form;
        form.validateFields((err, values) => {
            if (err) {
                return;
            }
            //TODO 处理保存。注意copy值的问题
            this.setState({
                formVisible: false
            })
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

    render() {
        const {columns, rowClassName, rowSelection, scrollX} = this.state;
        const WrappedAdvancedEntityForm = Form.create()(EntityForm);
        return (
          <div style={styles.tableContainer}>
            <Table
              dataSource={this.props.data}
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
            <WrappedAdvancedEntityForm ref={this.formRef} object={this.state.editorObject} visible={this.state.formVisible} fields={this.state.fields}
                onOk={this.handleSave} onCancel={this.handleCancel} />
          </div>
        );
    }
}
EntityListTable.prototypes = {
    tableRrn: PropTypes.number.isRequired,
    check: PropTypes.bool,
    rowClassName: PropTypes.func,
    rowkey: PropTypes.string,
    pagination: PropTypes.pagination
}

const styles = {
    tableContainer: {
      background: '#fff',
      paddingBottom: '10px',
    },
    highlightRow : {
      backgroundo: '#ff0000'
    },
    editIcon: {
      color: '#999',
      cursor: 'pointer',
    },
    circle: {
      display: 'inline-block',
      background: '#28a745',
      width: '8px',
      height: '8px',
      borderRadius: '50px',
      marginRight: '4px',
    },
    stateText: {
      color: '#28a745',
    },
    opration: {
      width: '100%'
    }
};