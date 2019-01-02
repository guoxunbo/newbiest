import EntityListTable from "./EntityListTable";
import Field from '../../api/dto/ui/Field';
import {Application} from '../../api/Application'
import { DefaultRowKey } from "../../api/const/ConstDefine";

/**
 * 具有选择框的table。不具备操作列
 */

export default class EntityListCheckTable extends EntityListTable {

    static displayName = 'EntityListCheckTable';

    constructor(props) {
        super(props);
    }

    /**
     * 默认的table框的选择框属性 此处不实现。
     */
    getRowSelection = (selectedRowKeys) => {
        const rowSelection = {
            columnWidth: Application.table.checkBox.width,
            fixed: true,
            selectedRowKeys,
            onChange: (selectedRowKeys, selectedRows) => {
                this.setState({
                    selectedRowKeys: selectedRowKeys,
                    selectedRows: selectedRows
                })
            }
        }
        return rowSelection;
    }

    selectRow = (record) => {
        const selectedRowKeys = [...this.state.selectedRowKeys];
        const selectedRows = [...this.state.selectedRows];
        if (selectedRowKeys.indexOf(record.objectRrn) >= 0) {
            selectedRowKeys.splice(selectedRowKeys.indexOf(record.objectRrn), 1);
        } else {
            selectedRowKeys.push(record.objectRrn);
        }

        if (selectedRows.indexOf(record) >= 0) {
            selectedRows.splice(selectedRows.indexOf(record), 1);
        } else {
            selectedRows.push(record);
        }
        this.setState({ 
            selectedRowKeys: selectedRowKeys,
            selectedRows: selectedRows
        });
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
        scrollX += Application.table.checkBox.width;
        let operationColumn = this.buildOperationColumn();
        scrollX += operationColumn.width;
        columns.push(operationColumn);
        return {
            columns: columns,
            scrollX: scrollX
        };
    }
}