import { Table, Popconfirm, Form } from 'antd';
import * as PropTypes from 'prop-types';
import TableManagerRequestBody from '../../../api/table-manager/TableManagerRequestBody';
import TableManagerRequestHeader from '../../../api/table-manager/TableManagerRequestHeader';
import Request from '../../../api/Request';
import {UrlConstant} from '../../../api/const/ConstDefine'
import MessageUtils from '../../../api/utils/MessageUtils';
import Field from '../../../api/dto/ui/Field';
import '../ListTable.scss';
import {Application} from '../../../api/Application';
const EditableContext = React.createContext();

const EditableRow = ({ form, index, ...props }) => (
  <EditableContext.Provider value={form}>
    <tr {...props} />
  </EditableContext.Provider>
);

const EditableFormRow = Form.create()(EditableRow);

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

export default class EditableTable extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
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
    let requestBody = TableManagerRequestBody.buildGetDataByName(this.props.refTableName, this.props.whereClause);
    let requestHeader = new TableManagerRequestHeader();
    let request = new Request(requestHeader, requestBody, UrlConstant.TableMangerUrl);
    let requestObject = {
      request: request,
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
    MessageUtils.sendRequest(requestObject);
  }

  buildColumn = (fields) => {
    let columns = [];
    let scrollX = 0;
    console.log(this.props.form);
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
        field: {},
        render: (text, record) => {
            const editable = this.isEditing(record);
            return (
              <div>
                {editable ? (
                  <span>
                    <EditableContext.Consumer>
                      {form => (
                        <a
                          href="javascript:;"
                          onClick={() => this.save(form, record.objectRrn)}
                          style={{ marginRight: 8 }}
                        >
                          Save
                        </a>
                      )}
                    </EditableContext.Consumer>
                    <Popconfirm
                      title="Sure to cancel?"
                      onConfirm={() => this.cancel(record.objectRrn)}
                    >
                      <a>Cancel</a>
                    </Popconfirm>
                  </span>
                ) : (
                  <a onClick={() => this.edit(record.objectRrn)}>Edit</a>
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

  edit(objectRrn) {
    this.setState({ editingKey: objectRrn });
  }

  save(form, key) {
    form.validateFields((error, row) => {
      if (error) {
        return;
      }
      //TODO 保存 暂时不实现
      console.log(row);
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

  render() {
    const {tableData, scrollX, rowClassName} = this.state;

    const components = {
      body: {
        row: EditableFormRow,
        cell: EditableCell,
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
      <Table
        rowKey={"objectRrn"}
        components={components}
        bordered
        pagination={Application.table.pagination}
        dataSource={tableData}
        columns={columns}
        scroll = {{ x: scrollX, y: 350 }}
        rowClassName={rowClassName.bind(this)}
      />
    );
  }
}

EditableTable.prototypes = {
    refTableName: PropTypes.string,
    editFlag: PropTypes.bool,
    whereClause: PropTypes.string
}
