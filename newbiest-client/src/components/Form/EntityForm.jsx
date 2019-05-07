import React, { Component } from 'react';
import { Modal, Form, Input, Row, Col, Tabs } from 'antd';
import * as PropTypes from 'prop-types';
import Field from '../../api/dto/ui/Field';
import Tab from '../../api/dto/ui/Tab';
import EntityManagerRequest from '../../api/entity-manager/EntityManagerRequest';
import I18NUtils from '../../api/utils/I18NUtils';
import { i18NCode } from '../../api/const/i18n';
import PropertyUtils from '../../api/utils/PropertyUtils';
import { DefaultOrderKey, DefaultRowKey, DateFormatType } from '../../api/const/ConstDefine';
import moment from 'moment';

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

    buildBasicSectionField = () => {
        const fields = this.props.table.fields;
        const formObject = this.props.object;
        const formItemLayout = {
            labelCol: {span: 6},
            wrapperCol: {span: 18},
        };
        let children = [];
        for (let f of fields) {
            let field = new Field(f, this.props.form);
            if (field.basicFlag && field.displayFlag && field.name != "objectRrn") {
                children.push(<Col span={12} key={field.objectRrn}>
                    {field.buildFormItem(formItemLayout, this.state.editFlag, undefined, formObject[field.name])}
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
            let formObject = this.props.object;
            PropertyUtils.copyProperties(values, formObject);
            // 如果当前values具备seqNo栏位并且该栏位没手动给值。则说明需要自动给个seqNo的值
            if (formObject.hasOwnProperty(DefaultOrderKey) && !formObject[DefaultOrderKey]) {
                // 只有对象有seqNo栏位，则tableData必定有seqNo
                if (this.props.tableData && Array.isArray(this.props.tableData)) {
                    if (this.props.tableData.length == 0) {
                        formObject[DefaultOrderKey] = 1;
                    } else {
                        let data = this.props.tableData.sort(function(a,b) {
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
            for (let property in formObject) {
                if (formObject[property] && moment.isMoment(formObject[property])) {
                    // 如果是单独的时间类型，不是个区域时间(dateFromTo)的话
                    formObject[property] = formObject[property].format(DateFormatType.DateTime)
                }
            }
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

    buildForm = () =>  {
        const {getFieldDecorator} = this.props.form;
        return (
            <Form>
                {getFieldDecorator(DefaultRowKey,
                {
                    initialValue: this.props.object ? this.props.object[DefaultRowKey] : undefined
                }
                )(<Input type='hidden'/>)}
                {this.buildBasicSection()}
                {this.buildTabs()}
            </Form>)
    }

    render() {
        const width = this.props.width || 1040;
        const title = this.props.title || I18NUtils.getClientMessage(i18NCode.Edit);
        const okText = this.props.okText || I18NUtils.getClientMessage(i18NCode.Ok);
        return (
            <div>
                <Modal centered 
                    width={width}
                    title={title} 
                    object={this.props.object} 
                    visible={this.props.visible} 
                    maskClosable={false} 
                    onOk={this.handleOk}
                    onCancel={this.props.onCancel} 
                    okText={okText}>
                    {this.buildForm()}
                </Modal>
            </div>
        );
    }
}

EntityForm.propTypes={
    title: PropTypes.string,
    visible: PropTypes.bool,
    object: PropTypes.object,
    onCancel: PropTypes.func,
    onOk: PropTypes.func,
    table: PropTypes.object,
    tableData: PropTypes.array
}

