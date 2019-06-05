import React from 'react';

import './ListTable.scss';
import EntityListTable from './EntityListTable';
import DynaxAnalyseRequest from '../../api/rtm-dynax-manager/DynaxAnalyseRequest';
import { Upload,Button } from 'antd';
import I18NUtils from '../../api/utils/I18NUtils';
import { i18NCode } from '../../api/const/i18n';

/**
 * 分析文件。只具备修改
 */
export default class DynaxAnalyseTable extends EntityListTable {

    static displayName = 'DynaxAnalyseTable';

    /**
     * 创建导入按钮 只能导入TXT
     */
    createImportButton = () => {
        return (<Upload key="import" accept=".txt" 
                    customRequest={(option) => this.handleUpload(option)} showUploadList={false} >
                    <Button type="primary" style={styles.tableButton} icon="file-add">{I18NUtils.getClientMessage(i18NCode.BtnImp)}</Button>
                </Upload>);
    }

    createButtonGroup = () => {
        let buttons = [];
        buttons.push(this.createImportButton());
        return buttons;
    }

    /**
     * 分析数据的表不具备删除操作
     */
    buildOperation = (record) => {
        let operations = [];
        operations.push(this.buildEditButton(record));
        return operations;
    }

    handleUpload = (option) => {
        let object = {}
        DynaxAnalyseRequest.sendImportRequest(object, option.file);
    }

}

const styles = {
    tableButton: {
        marginLeft:'20px'
    }
};
