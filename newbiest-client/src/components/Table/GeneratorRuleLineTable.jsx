import EntityListTable from "./EntityListTable";
import { Button, Form } from "antd";
import TableManagerRequest from "../../api/table-manager/TableManagerRequest";
import TableObject from '../../api/dto/ui/Table';
import IconUtils from "../../api/utils/IconUtils";
import EntityForm from "../Form/EntityForm";

const DataType = {
    FixedString: "F",
    Date: "D",
    Variable: "V",
    Sequence: "S"

}
const TableName = {
    FixedString: "COMFixedStringLine",
    DateLine: "COMDateLine",
    VariableLine: "COMVariableLine",
    SequenceLine: "COMSequenceLine",
}
export default class GeneratorRuleLineTable extends EntityListTable {

    static displayName = 'GeneratorRuleLineTable';

    constructor(props) {
        super(props);
        let state = Object.assign(this.state, {
            formTable: {fields: []}
        });
        this.state = state;
    }
      
    /**
     * 重写此方法。因为当前的Table不是从props传递。
     */
    componentWillReceiveProps = () => {

    }
    
    componentDidMount = () => {
        let tab = this.props.tab;
        let self = this;
        let whereClause = " 1 != 1";
        if (tab.whereClause && this.props.parentObject) {
            whereClause = tab.whereClause.format(this.props.parentObject);
        }
        let requestObject = {
            tableName: tab.refTableName,
            whereClause: whereClause,
            success: function(responseBody) {
                let table = responseBody.table;
                let columnData = self.buildColumn(table.fields);
                self.setState({
                    data: responseBody.dataList,
                    table: table,
                    columns: columnData.columns,
                    scrollX: columnData.scrollX,
                    loading: false
                });
            }
        }
        TableManagerRequest.sendGetDataByNameRequest(requestObject);  
    }

    /**
     * 此处因为点击不同的Button要展现不同的form信息。故此处的table用formTable去代替
     */
    createForm = () => {
        const WrappedAdvancedEntityForm = Form.create()(EntityForm);
        return  <WrappedAdvancedEntityForm ref={this.formRef} object={this.state.editorObject} visible={this.state.formVisible} 
                                            table={this.state.formTable} tableData={this.state.data} onOk={this.refresh} onCancel={this.handleCancel} />
    }

    handleEdit = (record) => {
        this.handleButtonClick(record.dataType, record);
    }

    /**
     * TODO 后续需要优化。尽量把table的信息放到state中，如果存在就直接取，不需要去再发起请求
     */
    handleButtonClick = (dataType, record) => {
        let self = this;
        let tableName = "";
        if (DataType.FixedString === dataType) {
            tableName = TableName.FixedString
        } else if (DataType.Date === dataType) {
            tableName = TableName.DateLine
        } else if (DataType.Variable === dataType) {
            tableName = TableName.VariableLine
        } else if (DataType.Sequence === dataType) {
            tableName = TableName.SequenceLine
        }
        let requestObject = {
            name: tableName,
            success: function(responseBody) {
                let table = responseBody.table;
                let editorObject = record ? record : TableObject.buildDefaultModel(table.fields, self.props.parentObject);
                self.setState({
                    formTable: responseBody.table,
                    editorObject: editorObject,
                    formVisible : true
                });
            }
        }
        TableManagerRequest.sendGetByNameRequest(requestObject);
    }

    createButtonGroup = () => {
        if (!this.props.parentObject.objectRrn) {
            return;
        }
        return (<div>
                    <Button style={{marginRight:'1px', marginLeft:'10px'}} size="small" href="javascript:;" 
                        onClick={() => this.handleButtonClick(DataType.FixedString)}>
                        {IconUtils.buildIcon("icon-guding")}F</Button>
                    <Button style={{marginRight:'1px', marginLeft:'10px'}} size="small" href="javascript:;"
                        onClick={() => this.handleButtonClick(DataType.Date)}>
                        {IconUtils.buildIcon("icon-riqi")}D</Button>
                    <Button style={{marginRight:'1px', marginLeft:'10px'}} size="small" href="javascript:;"
                        onClick={() => this.handleButtonClick(DataType.Variable)}>
                        {IconUtils.buildIcon("icon-canshu")}V</Button>
                    <Button style={{marginRight:'1px', marginLeft:'10px'}} size="small" href="javascript:;"
                        onClick={() => this.handleButtonClick(DataType.Sequence)}>
                        {IconUtils.buildIcon("icon-xulie")}S</Button>
                </div>);
    }

}