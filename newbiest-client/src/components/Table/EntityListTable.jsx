import  React, { Component } from 'react';

import { Table, Popconfirm, Button,Form, Dropdown, Menu, Icon } from 'antd';
import './ListTable.scss';
import {Application, SessionContext} from '../../api/Application'
import {DefaultRowKey, Language} from '../../api/const/ConstDefine'
import MessageUtils from '../../api/utils/MessageUtils';
import Field from '../../api/dto/ui/Field';
import EntityForm from '../Form/EntityForm';
import * as PropTypes from 'prop-types';
import TableObject from '../../api/dto/ui/Table';
import EntityManagerRequest from '../../api/entity-manager/EntityManagerRequest';
import I18NUtils from '../../api/utils/I18NUtils';
import { i18NCode } from '../../api/const/i18n';
import TableManagerRequest from '../../api/table-manager/TableManagerRequest';

const ExpMenuKey = {
    exportTemplate: "exportTemplate",
    exportData: "exportData"
}
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
        let operationColumn = this.buildOperationColumn(scrollX);
        scrollX += operationColumn.width;
        columns.push(operationColumn);
        return {
            columns: columns,
            scrollX: scrollX
        };
    }

    buildOperationColumn(scrollX) {
        let maxWidth = document.querySelector('.custom-table').clientWidth;
        let self = this;
        let oprationColumn = {
            key: "opration",
            title: "opration",
            dataIndex: "opration",
            align: "center",
            fixed: maxWidth > scrollX + Application.table.oprationColumn.width ? false : 'right',
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

    /**
     * 更新表格数据
     * @param responseData 数据如用户、
     */
    refresh = (responseData) => {
        var self = this;
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

    handleExpMenuClick({ key }) {
        const {table} = this.state;
        let language = SessionContext.getLanguage();

        let fileName = table.name;
        if (language == undefined) {
            language = Language.Chinese;
        }
        if (language == Language.Chinese) {
            fileName = table.labelZh;
        } else if (language == Language.English) {
            fileName = table.label;
        }
        let object = {
            tableRrn: table.objectRrn,
            fileName: fileName + ".xls",
        }
        if (ExpMenuKey.exportTemplate === key) {
            TableManagerRequest.sendExportRequest(object, true);
        } else if (ExpMenuKey.exportData === key) {
            TableManagerRequest.sendExportRequest(object);
        }
    }

    /**
     * 创建btn组。不同的table对button的组合要求不一样时。可以重载其方法做处理
     */
    createButtonGroup() {
        const exportMenu = (
            <Menu onClick={this.handleExpMenuClick.bind(this)}>
              <Menu.Item key={ExpMenuKey.exportData}>
                <Icon type="database" /> {I18NUtils.getClientMessage(i18NCode.BtnExpData)}
              </Menu.Item>
              <Menu.Item key={ExpMenuKey.exportTemplate}>
                <Icon type="file-excel" />{I18NUtils.getClientMessage(i18NCode.BtnExpTemplate)}
              </Menu.Item>
            </Menu>
        );

        let buttons = [];
        buttons.push(<Button key="add" type="primary" style={styles.tableButton} icon="plus" onClick={() => this.handleAdd()}>{I18NUtils.getClientMessage(i18NCode.BtnAdd)}</Button>);
        buttons.push(<Button key="import" type="primary" style={styles.tableButton} icon="file-add" onClick={() => this.handleAdd()}>{I18NUtils.getClientMessage(i18NCode.BtnImp)}</Button>);
        
        buttons.push(<Dropdown key="export" overlay={exportMenu}>
                        <Button type="primary" style={styles.tableButton} icon="export" >
                            {I18NUtils.getClientMessage(i18NCode.BtnExp)} <Icon type="down" />
                        </Button>
                    </Dropdown>);
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
                    scroll = {{ x: scrollX }}
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