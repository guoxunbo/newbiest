import { Table, Popconfirm, Button, Form } from 'antd';
import * as PropTypes from 'prop-types';
import {DefaultRowKey, DateFormatType} from '../../../api/const/ConstDefine'
import MessageUtils from '../../../api/utils/MessageUtils';
import Field from '../../../api/dto/ui/Field';
import '../ListTable.scss';
import {Application} from '../../../api/Application';
import TableObject from '../../../api/dto/ui/Table';
import PropertyUtils from '../../../api/utils/PropertyUtils';
import uuid from 'react-native-uuid';
import EntityManagerRequest from '../../../api/entity-manager/EntityManagerRequest'; 
import I18NUtils from '../../../api/utils/I18NUtils';
import { i18NCode } from '../../../api/const/i18n';
import TableManagerRequest from '../../../api/table-manager/TableManagerRequest';
import moment from 'moment';

const EditableContext = React.createContext();

class EditableCell extends React.Component {
  render() {
    const {
      editing,
      dataIndex,
      title,
      inputType,
      record,
      index,
      field,
      ...restProps
    } = this.props;
    return (
      <EditableContext.Consumer>
        {(form) => {
          let formItem =null;
          if (editing) {
            formItem = field.buildTableFormItem(record, form);
          }
          return (
            <td {...restProps}>
              {editing ? formItem : restProps.children}
            </td>
          );
        }}
      </EditableContext.Consumer>
    );
  }
}

class EditableTable extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      table:{},
      tableData:[],
      editingKey: '', 
      columns:[], 
      scrollX: undefined,
      rowClassName: (record, index) => {},
    };
  }

  componentDidMount = () => {
    this.buildTable();
  }

  buildTable = () => {
    const self = this;
    let requestObject = {
      tableName: this.props.refTableName,
      whereClause: this.props.whereClause,
      success: function(responseBody) {
        let table = responseBody.table;
        let columnData = self.buildColumn(table.fields);
        self.setState({
          tableData: responseBody.dataList,
          table: table,
          columns: columnData.columns,
          scrollX: columnData.scrollX,
          rowClassName: (record, index) => self.getRowClassName(record, index),
        });
      }
    }
    TableManagerRequest.sendGetDataByNameRequest(requestObject);
  }

  buildColumn = (fields) => {
    let columns = [];
    let scrollX = 0;
    for (let field of fields) {
        let f  = new Field(field, this.props.form);
        let column = f.buildColumn();
        if (column != null) {
          // 如果可以编辑
          if (f.editable) {
            column.editable = true;     
          }
          column.field = f;
          columns.push(column);
          scrollX += column.width;
        }
        
    }
    if (this.props.editFlag) {
      let oprationColumn = this.buildOperationColumn();
      scrollX += oprationColumn.width;
      columns.push(oprationColumn);
    }
    return {
        columns: columns,
        scrollX: scrollX
    };
  }

  buildOperationColumn = () => {
      let operationColumn = {
        editable: false,
        title: 'operation',
        dataIndex: 'operation',
        fixed: 'right',
        width: Application.table.oprationColumn.width,
        render: (text, record) => {
            return (
              <div>
                {this.isEditing(record) ? (
                  <span>
                    <EditableContext.Consumer>
                      {form => (
                        <Button style={{marginRight:8}} icon="save" 
                          onClick={() => this.save(form, record)} size="small" href="javascript:;"></Button>
                      )}
                    </EditableContext.Consumer>
                    <Popconfirm title={I18NUtils.getClientMessage(i18NCode.ConfirmCancel)} onConfirm={() => this.cancel(record.objectRrn)}>
                      <Button style={{marginRight:8}} icon="close-circle" size="small" href="javascript:;"></Button>
                    </Popconfirm>
                  </span>
                ) : (
                  <div>
                    <Button style={{marginRight:'1px'}} icon="edit" onClick={() => this.edit(record)} size="small" href="javascript:;"></Button>
                    <Popconfirm title={I18NUtils.getClientMessage(i18NCode.ConfirmDelete)} onConfirm={() => this.handleDelete(record)}>
                      <Button icon="delete" size="small" type="danger"></Button>
                    </Popconfirm>
                  </div>    
                )}
              </div>
            );
          }
      }
      return operationColumn;
  }

  isEditing = (record) => {
    return record.objectRrn === this.state.editingKey;
  };

  edit(record) {
    this.setState({ editingKey: record.objectRrn });
  }

  /**
   * 保存一行数据
   * @param form 表单
   * @param rowData 当前行数据
   */
  save(form, rowData) {
    form.validateFields((error, record) => {
      if (error) {
        return;
      }
      PropertyUtils.copyProperties(record, rowData);
      for (let property in rowData) {
        if (rowData[property] && moment.isMoment(rowData[property])) {
            // 如果是单独的时间类型，不是个区域时间(dateFromTo)的话
            rowData[property] = rowData[property].format(DateFormatType.DateTime)
        }
      }
      let self = this;
      const { tableData, table } = this.state;
      let object = {
        modelClass : table.modelClass,
        values: rowData,
        success: function(responseBody) {
          let responseData = responseBody.data;
          let dataIndex = -1;
          if (rowData.objectRrn) {
            tableData.map((data, index) => {
              if (data.objectRrn == responseData.objectRrn) {
                  dataIndex = index;
              }
            });
          } else {
            tableData.map((data, index) => {
              if (!data.objectRrn) {
                  dataIndex = index;
              }
            });
          }
          tableData.splice(dataIndex, 1, responseData);
          
          self.setState({
            tableData: tableData,
            editingKey: ""
          }); 
          MessageUtils.showOperationSuccess();
        }
      }
      EntityManagerRequest.sendMergeRequest(object);
    });
  }

  cancel = () => {
    this.setState({ editingKey: '' });
  };

  getRowClassName = (record, index) => {
    if(index % 2 ===0) {
        return 'even-row'; 
    } else {
        return ''; 
    }
  };

  handleAdd = () => {
    const { tableData, table } = this.state;
    // 新建的时候如果有栏位是来源于父值的话，对其进行赋值
    const newData = TableObject.buildDefaultModel(table.fields, this.props.parentObject);
    newData[DefaultRowKey] = uuid.v1();
    newData["newFlag"] = true;

    this.setState({
      tableData: [...tableData, newData],
      editingKey: newData.objectRrn,
    });
  }

  handleDelete = (record) => {
    let {tableData, table } = this.state;
    let self = this;
    let dataIndex = tableData.indexOf(record);
    // 如果是新增的数据，没做过保存直接删除
    if (record.newFlag) {
      tableData.splice(dataIndex, 1);
      this.setState({
        tableData: tableData,
        editingKey: ""
      });
    } else {
      // 调用后台删除
      let object = {
        modelClass : table.modelClass,
        values: record,
        success: function(responseBody) {
          tableData.splice(dataIndex, 1);
          self.setState({
            tableData: tableData,
            editingKey: ""
          }) 
          MessageUtils.showOperationSuccess();
        }
      }
      EntityManagerRequest.sendDeleteRequest(object);
    }
  }

  createButtonGroup = () => {
    return (
      <div>
          <Button style={{marginRight:'1px', marginLeft:'10px'}} icon="plus" onClick={() => this.handleAdd()} size="small" href="javascript:;">添加</Button>
      </div>
     );
  }

  render() {
    const {tableData, scrollX, rowClassName} = this.state;

    const components = {
      body: {
        cell: EditableCell
      },
    };

    const columns = this.state.columns.map((col) => {
      if (!col.editable) {
        return col;
      }
      return {
        ...col,
        onCell: record => ({
          record,
          inputType: 'text',
          dataIndex: col.dataIndex,
          title: col.title,
          editing: this.isEditing(record),
          field: col.field
        }),
      };
    });
    return (
      
      
      <div>
          {(this.props.editFlag && !this.props.newFlag) ? this.createButtonGroup() : ''};
          <EditableContext.Provider value={this.props.form}>
          <Table
            rowKey={DefaultRowKey}
            components={components}
            bordered
            pagination={Application.table.pagination}
            dataSource={tableData}
            columns={columns}
            scroll = {{ x: scrollX, y: 350 }}
            rowClassName={rowClassName.bind(this)}
          />
        </EditableContext.Provider>
      </div>
    );
  }
}
export default Form.create()(EditableTable);

EditableTable.prototypes = {
    refTableName: PropTypes.string,
    editFlag: PropTypes.bool,
    whereClause: PropTypes.string,
    newFlag: PropTypes.bool,
    parentObject: PropTypes.object,
}
