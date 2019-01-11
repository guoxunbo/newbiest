import React, { Component } from 'react';
import { Modal, Form, Input, Row, Col, Tabs } from 'antd';
import * as PropTypes from 'prop-types';
import Field from '../../api/dto/ui/Field';
import Tab from '../../api/dto/ui/Tab';
import EntityManagerRequest from '../../api/entity-manager/EntityManagerRequest';
import I18NUtils from '../../api/utils/I18NUtils';
import { i18NCode } from '../../api/const/i18n';
import PropertyUtils from '../../api/utils/PropertyUtils';

export default class EntityForm extends Component {
    static displayName = 'EntityForm';

    constructor(props) {
        super(props);
        let editFlag = false;
        if (this.props.object && this.props.object.objectRrn) {
            editFlag = true;
        }
        this.state = {
            editFlag : editFlag
        };
    }  

    componentDidMount = () => {
        const object = this.props.object;
        if (object != undefined) {
            let fieldValue = {};
            const formItemNames = Object.keys(this.props.form.getFieldsValue());
            for (let name of formItemNames) {
                fieldValue[name] = object[name];
            }
            this.props.form.setFieldsValue(fieldValue);
        }
    }

    buildBasicSectionField = () => {
        const fields = this.props.table.fields;
        const formItemLayout = {
            labelCol: {span: 6},
            wrapperCol: {span: 18},
        };
        let children = [];
        for (let f of fields) {
            let field = new Field(f, this.props.form);
            if (field.basicFlag && field.displayFlag && field.name != "objectRrn") {
                children.push(<Col span={12} key={field.objectRrn}>
                    {field.buildFormItem(formItemLayout, this.state.editFlag)}
                </Col>);
            }
        }
        return children;
    }

    buildTabs = () => {
        const tabs = this.props.table.tabs;
        const tabPanels = [];
        const formItemLayout = {
            labelCol: {span: 6},
            wrapperCol: {span: 18},
        };
        if (Array.isArray(tabs)) {
            tabs.forEach((tab) => {
                let tabPanel = new Tab(tab);
                tabPanels.push(tabPanel.buildTab(this.props.form, formItemLayout, this.props.object));
            }) 
        }
        return (<Tabs>
           {tabPanels}
        </Tabs>)
    }
    
    buildBasicSection =() => {
        return (
            <div>
                <h2 className="section-title">{I18NUtils.getClientMessage(i18NCode.BasicInfo)}</h2>
                <Row gutter={16}>
                    {this.buildBasicSectionField()}
                </Row>
            </div>
        )
    }

    handleOk = (e) => {
        const form = this.props.form;
        form.validateFields((err, values) => {
            if (err) {
                return;
            }
            PropertyUtils.copyProperties(values, this.props.object)
            this.handleSave(this.props.object);
        });
    }

    handleSave = () => {
        var self = this;
        // 默认处理的saveEntity
        let object = {
            modelClass: this.props.table.modelClass,
            values: this.props.object,
            success: function(responseBody) {
                if (self.props.onOk) {
                    self.props.onOk(responseBody.data);
                }
            }
        };
        EntityManagerRequest.sendMergeRequest(object);
    }

    buildForm = () =>  {
        const {getFieldDecorator} = this.props.form;
        return (
            <Form>
                {getFieldDecorator('objectRrn')(<Input type='hidden'/>)}
                {this.buildBasicSection()}
                {this.buildTabs()}
            </Form>)
    }

    render() {
        return (
            <div>
                <Modal width={1040} centered title={I18NUtils.getClientMessage(i18NCode.Edit)} object={this.props.object} visible={this.props.visible} 
                    maskClosable={false} onOk={this.handleOk} onCancel={this.props.onCancel} okText={I18NUtils.getClientMessage(i18NCode.Ok)} 
                    cancelText={I18NUtils.getClientMessage(i18NCode.Cancel)}>
                    {this.buildForm()}
                </Modal>
            </div>
        );
    }
}

EntityForm.propTypes={
    visible: PropTypes.bool,
    object: PropTypes.object,
    onCancel: PropTypes.func,
    onOk: PropTypes.func,
    table: PropTypes.object
}

