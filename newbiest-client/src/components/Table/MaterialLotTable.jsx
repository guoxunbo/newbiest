import React from 'react';

import './ListTable.scss';
import MaterialForm from '../Form/MaterialForm';
import EntityListTable from './EntityListTable';
import { Form, Button } from 'antd';
import I18NUtils from '../../api/utils/I18NUtils';
import { i18NCode } from '../../api/const/i18n';
import TableManagerRequest from '../../api/table-manager/TableManagerRequest';
import TableObject from '../../api/dto/ui/Table';
import ReceiveMaterialForm from '../Form/ReceiveMaterialForm';
import MessageUtils from '../../api/utils/MessageUtils';

export default class MaterialLotTable extends EntityListTable {

    static displayName = 'MaterialLotTable';

    constructor(props) {
        super(props);
    }
    
    
    /**
     * 创建btn组。不同的table对button的组合要求不一样时。可以重载其方法做处理
     */
    createButtonGroup = () => {
        let buttons = [];
        buttons.push(this.createExportDataButton());
        return buttons;
    }

    /**
     * 物料批次不可更改，不可删除
     */
    buildOperationColumn = () => {

    }

}