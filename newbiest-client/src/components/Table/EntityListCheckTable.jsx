import EntityListTable from "./EntityListTable";
import Field from '../../api/dto/ui/Field';
import {Application} from '../../api/Application'

/**
 * 具有选择框的table。不具备操作列
 */

export default class EntityListCheckTable extends EntityListTable {

    static displayName = 'EntityListCheckTable';

    constructor(props) {
        super(props);
    }

    componentWillMount = () => {
        this.setState({
            rowClassName: (record, index) => this.getRowClassName(record, index),
            rowSelection: this.getRowSelection(), 
        });
    }

    // 默认的table框的选择框属性
    getRowSelection = () => {
        const rowSelection = {
            columnWidth: Application.table.checkBox.width,
            fixed: true,
            onChange: (selectedRowKeys, selectedRows) => {
                this.setState({
                    selectedRowKeys: selectedRowKeys,
                    selectedRows: selectedRows
                })
            }
        }
        return rowSelection;
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