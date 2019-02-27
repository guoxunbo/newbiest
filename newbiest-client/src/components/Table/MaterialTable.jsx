import React from 'react';

import './ListTable.scss';
import MaterialForm from '../Form/MaterialForm';
import EntityListTable from './EntityListTable';
import { Form } from 'antd';

export default class MaterialTable extends EntityListTable {

    static displayName = 'MaterialTable';

    createForm = () => {
        const WrappedAdvancedMaterialForm = Form.create()(MaterialForm);
        return  <WrappedAdvancedMaterialForm ref={this.formRef} object={this.state.editorObject} visible={this.state.formVisible} 
                                            table={this.state.table} onOk={this.refresh} onCancel={this.handleCancel} />
    }
    
}