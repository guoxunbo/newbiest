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
     * 默认的table框的选择框属性
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

        let checkIndex = selectedRowKeys.indexOf(record.objectRrn);
        if (checkIndex >= 0) {
            selectedRowKeys.splice(checkIndex, 1);
            selectedRows.splice(checkIndex, 1);
        } else {
            selectedRowKeys.push(record.objectRrn);
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
        let operationColumn = this.buildOperationColumn(scrollX);
        scrollX += operationColumn.width;
        columns.push(operationColumn);
        return {
            columns: columns,
            scrollX: scrollX
        };
    }
}