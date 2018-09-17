import React, { Component } from 'react';
import { Table } from 'antd';
import './EntityListTable.scss';
import {Application} from '../../js/Application'
import {DefaultRowKey, Type} from '../../js/const/ConstDefine'
import {TableManagerRequestBody} from '../../api/table-manager/TableManagerRequestBody';
import {TableManagerRequestHeader} from '../../api/table-manager/TableManagerRequestHeader';
import {Request} from '../../js/dataModel/Request';
import {UrlConstant} from "../../js/const/ConstDefine";
import {MessageUtils} from '../../js/MessageUtils';
import Field from '../../api/dto/ui/Table';

export default class EntityListTable extends Component {

    static displayName = 'EntityListTable';

    constructor(props) {
        super(props);
        let tableRrn = this.props.tableRrn;
        let rowClassname = this.props.rowClassName;
        let check = this.props.check;
        this.state = {
            tableRrn: tableRrn,
            columns: [],
            data: this.getData(this.props.tableRrn),
            pagination: this.props.pagination == null ? Application.pagination : this.props.pagination,
            rowkey: this.props.rowkey == null ? DefaultRowKey : this.props.rowkey,
            scollx: "",
            rowClassName: (rowClassname == null || typeof rowClassname != Type.function) ? 
                            (record, index) => this.getRowClassName(record, index) : this.props.rowClassName,
            loading: true,
            // 是否带有选择框
            check: check,
            rowSelection: undefined,
            selectedRowKeys: [],
            selectedRows: []

        };
    }

    componentDidMount() {
        this.setState({
            columns: this.getColumns(this.state.tableRrn),
            rowSelection: this.getRowSelection(),
        })
    }
    
    getRowClassName = (record, index) => {
        if(index % 2 ===0) {
            return 'even-row'; 
        } else {
            return ''; 
        }
    };

    // 默认的table框的选择框属性
    getRowSelection = () => {
        const rowSelection = {
            // TODO 不知道为啥这几个属性不生效，设置了fixed会出现2个checkbox。待确认
            // columnWidth: '10',
            // fixed: true,
            // hideDefaultSelections: true,
            onChange: (selectedRowKeys, selectedRows) => {
                this.setState({
                    selectedRowKeys: selectedRowKeys,
                    selectedRows: selectedRows
                })
            },
        }
        return rowSelection;
    }

    getData = (tableRrn) => {
        let self = this;
        let requestBody = TableManagerRequestBody.buildGetData(tableRrn);
        let requestHeader = new TableManagerRequestHeader();
        let request = new Request(requestHeader, requestBody, UrlConstant.TableMangerUrl);
        let requestObject = {
            request: request,
            success: function(responseBody) {
                self.setState({
                    data: responseBody.dataList,
                    loading: false,
                });
            }
          }
        MessageUtils.sendRequest(requestObject);
    }

    getColumns = (tableRrn) => {
        let self = this;
        let requestBody = TableManagerRequestBody.buildGetByRrn(tableRrn);
        let requestHeader = new TableManagerRequestHeader();
        let request = new Request(requestHeader, requestBody, UrlConstant.TableMangerUrl);
        let requestObject = {
            request: request,
            success: function(responseBody) {
                let fields = responseBody.table.fields;
                let columns = [];
                let scollx;
                for (let field of fields) {
                    let f  = new Field(field);
                    let column = f.buildColumn();
                    if (column != null) {
                        scollx += column.width;
                        columns.push(column);
                    }
                }
                self.setState({
                    columns: columns,
                    scollx: scollx
                });
            }
          }
        MessageUtils.sendRequest(requestObject);
    }

    render() {
        return (
          <div style={styles.tableContainer}>
            <Table
              dataSource={this.state.data}
              bordered
              className="custom-table"
              pagination={this.state.pagination}
              columns = {this.state.columns}
              scroll = {{ y: 350, x: this.state.scollx }}
              rowKey = {this.state.rowkey}
              loading = {this.state.loading}
              rowClassName = {this.state.rowClassName.bind(this)}
              rowSelection = {this.state.check ? this.state.rowSelection : undefined}
            >
            </Table>
          </div>
        );
    }
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
};