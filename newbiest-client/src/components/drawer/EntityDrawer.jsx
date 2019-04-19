import  React, { Component } from 'react';

import { Drawer, Form, Tabs, Row, Input, Col, Button } from "antd";
import I18NUtils from "../../api/utils/I18NUtils";
import { i18NCode } from "../../api/const/i18n";
import Tab from '../../api/dto/ui/Tab';
import Field from '../../api/dto/ui/Field';
import PropertyUtils from '../../api/utils/PropertyUtils';
import { DefaultOrderKey, DefaultRowKey } from '../../api/const/ConstDefine';
import EntityManagerRequest from '../../api/entity-manager/EntityManagerRequest';

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
        if (object) {
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
               {getFieldDecorator(DefaultRowKey,{
                    initialValue: this.props.object[DefaultRowKey]
                })(<Input type='hidden'/>)}
                {this.buildBasicSection()}
                {this.buildTabs()}
            </Form>)
    }

    handleOk = (e) => {
        const form = this.props.form;
        form.validateFields((err, values) => {
            if (err) {
                return;
            }
            let formObject = this.props.object;
            PropertyUtils.copyProperties(values, formObject);
            // 如果当前values具备seqNo栏位并且该栏位没手动给值。则说明需要自动给个seqNo的值
            if (formObject.hasOwnProperty(DefaultOrderKey) && !formObject[DefaultOrderKey]) {
                // 只有对象有seqNo栏位，则tableData必定有seqNo
                if (this.props.tableData && Array.isArray(this.props.tableData)) {
                    if (this.props.tableData.length == 0) {
                        formObject[DefaultOrderKey] = 1;
                    } else {
                        let data = this.props.tableData.sort(function(a,b){
                            if (a[DefaultOrderKey] - b[DefaultOrderKey] < 0) {
                                return -1;
                            } else {
                                return 1;
                            }
                        });
                        formObject[DefaultOrderKey] = data[data.length - 1][DefaultOrderKey] + 1;
                    }
                }
            }
            // alert(formObject);
            this.handleSave(formObject);
        });
    }

    handleSave = (formObject) => {
        var self = this;
        // 默认处理的saveEntity
        let object = {
            modelClass: this.props.table.modelClass,
            values: formObject,
            success: function(responseBody) {
                if (self.props.onOk) {
                    self.props.onOk(responseBody.data);
                }
            }
        };
        EntityManagerRequest.sendMergeRequest(object);
    }

    render() {
        return <Drawer width={1040} onClose={this.props.onDrawerClose} object={this.props.object}
                    visible={this.props.visible} title={I18NUtils.getClientMessage(i18NCode.Edit)}>
                    {this.buildForm()}
                    <div style={{position: 'absolute',
                                    left: 0,
                                    bottom: 0,
                                    width: '100%',
                                    borderTop: '1px solid #e9e9e9',
                                    padding: '10px 16px',
                                    background: '#fff',
                                    textAlign: 'right'}}>
                    <Button onClick={this.handleOk} type="primary" > {I18NUtils.getClientMessage(i18NCode.Ok)} </Button>
                    </div>
                </Drawer>
    }
}

