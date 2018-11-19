import { Table, Input, InputNumber, Popconfirm, Form } from 'antd';
import * as PropTypes from 'prop-types';
import TableManagerRequestBody from '../../../api/table-manager/TableManagerRequestBody';
import TableManagerRequestHeader from '../../../api/table-manager/TableManagerRequestHeader';
import Request from '../../../api/Request';
import {UrlConstant} from '../../../api/const/ConstDefine'
import MessageUtils from '../../../api/utils/MessageUtils';
import Field from '../../../api/dto/ui/Field';
import '../ListTable.scss';

const FormItem = Form.Item;
const EditableContext = React.createContext();

const EditableRow = ({ form, index, ...props }) => (
  <EditableContext.Provider value={form}>
    <tr {...props} />
  </EditableContext.Provider>
);

const EditableFormRow = Form.create()(EditableRow);

class EditableCell extends React.Component {
  getInput = () => {
    if (this.props.inputType === 'number') {
      return <InputNumber />;
    }
    return <Input />;
  };
  render() {
    const {
      editing,
      dataIndex,
      title,
      inputType,
      record,
      index,
      ...restProps
    } = this.props;
    return (
      <EditableContext.Consumer>
        {(form) => {
          const { getFieldDecorator } = form;
          return (
            <td {...restProps}>
              {editing ? (
                <FormItem style={{ margin: 0 }}>
                  {getFieldDecorator(dataIndex, {
                    rules: [{
                      required: true,
                      message: `Please Input ${title}!`,
                    }],
                    initialValue: record[dataIndex],
                  })(this.getInput())}
                </FormItem>
              ) : restProps.children}
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
        console.log("_" +  columnData.scrollX);
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
          // 如果可以编辑
          if (f.editable) {
            column.editable = true;     
          }
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
                          onClick={() => this.save(form, record.key)}
                          style={{ marginRight: 8 }}
                        >
                          Save
                        </a>
                      )}
                    </EditableContext.Consumer>
                    <Popconfirm
                      title="Sure to cancel?"
                      onConfirm={() => this.cancel(record.key)}
                    >
                      <a>Cancel</a>
                    </Popconfirm>
                  </span>
                ) : (
                  <a onClick={() => this.edit(record.key)}>Edit</a>
                )}
              </div>
            );
          }
      }
      return operationColumn;
  }

  isEditing = (record) => {
    return record.key === this.state.editingKey;
  };

  edit(key) {
    this.setState({ editingKey: key });
  }

  save(form, key) {
    form.validateFields((error, row) => {
      if (error) {
        return;
      }
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
        }),
      };
    });
    return (
      <Table
        rowKey={"objectRrn"}
        components={components}
        bordered
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
