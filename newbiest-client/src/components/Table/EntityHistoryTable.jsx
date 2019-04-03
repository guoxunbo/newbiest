import React from 'react';

import './ListTable.scss';
import EntityListTable from './EntityListTable';

/**
 * 所有历史表的父表。只能导出，不具备新增等其他功能
 */
export default class EntityHistoryTable extends EntityListTable {

    static displayName = 'EntityHistoryTable';

    createButtonGroup = () => {
        let buttons = [];
        buttons.push(this.createExportDataButton());
        return buttons;
    }

    /**
     * 历史表不能有操作
     */
    buildOperationColumn = () => {
        
    }
}
