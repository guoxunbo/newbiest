import React, { Component } from 'react';
import { Modal, Form, Input, Row, Col, Tabs } from 'antd';
import * as PropTypes from 'prop-types';
import Field from '../../api/dto/ui/Field';
import Tab from '../../api/dto/ui/Tab';

export default class EntityForm extends Component {
    static displayName = 'EntityForm';

    constructor(props) {
        super(props);
        this.state = {
            editFlag : this.props.object.objectRrn ? true : false
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
            this.props.form.setFieldsValue(fieldValue)
        }
    }

    buildBasicSectionField = () => {
        const fields = this.props.fields;
        const { getFieldDecorator } = this.props.form;
        const formItemLayout = {
            labelCol: {span: 6},
            wrapperCol: {span: 18},
        };
        let children = [];
        for (let f of fields) {
            let field = new Field(f);
            if (field.basicFlag && field.displayFlag && field.name != "objectRrn") {
                children.push(<Col span={12} key={field.objectRrn}>
                    {field.buildFormItem(getFieldDecorator, formItemLayout, this.state.editFlag)}
                </Col>);
            }
        }
        return children;
    }

    buildTabs = () => {
        const tabs = this.props.tabs;
        const tabPanels = [];

        const { getFieldDecorator } = this.props.form;
        const formItemLayout = {
            labelCol: {span: 6},
            wrapperCol: {span: 18},
        };

        if (Array.isArray(tabs)) {
            tabs.forEach((tab) => {
                let tabPanel = new Tab(tab);
                tabPanels.push(tabPanel.buildTab(getFieldDecorator, formItemLayout, this.props.object));
            }) 
        }
        return (<Tabs>
           {tabPanels}
        </Tabs>)
    }
    
    buildBasicSection =() => {
        return (
            <div>
                <h2 className="section-title">基础信息</h2>
                <Row gutter={16}>
                    {this.buildBasicSectionField()}
                </Row>
            </div>
        )
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
                <Modal width={1040} centered title="编辑" object={this.props.object} visible={this.props.visible} confirmLoading={this.props.confirmLoading}
                    onOk={this.props.onOk} onCancel={this.props.onCancel}>
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
    confirmLoading: PropTypes.bool,
    fields: PropTypes.array,
    tabs: PropTypes.array
}

