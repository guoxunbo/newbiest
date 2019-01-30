import  React, { Component } from 'react';

import { Drawer, Form, Tabs, Row, Input, Col, Button } from "antd";
import I18NUtils from "../../api/utils/I18NUtils";
import { i18NCode } from "../../api/const/i18n";
import Tab from '../../api/dto/ui/Tab';
import Field from '../../api/dto/ui/Field';

export default class EntityDrawer extends Component {
    static displayName = 'EntityDrawer';

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
        return <Drawer width={1040} onClose={this.props.onDrawerClose} object={this.props.object}
                    visible={this.props.visible}  title={I18NUtils.getClientMessage(i18NCode.Edit)}>
                    {this.buildForm()}
                </Drawer>
    }


}