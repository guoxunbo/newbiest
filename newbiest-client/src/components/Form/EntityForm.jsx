import React, { Component } from 'react';
import { Modal, Form, Input, Row, Col, Tabs } from 'antd';
import * as PropTypes from 'prop-types';
import Field from '../../api/dto/ui/Field';

const TabPane = Tabs.TabPane;

export default class EntityForm extends Component {
    static displayName = 'EntityForm';

    constructor(props) {
        super(props);
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

    buildFields = () => {
        const fields = this.props.fields;
        const { getFieldDecorator } = this.props.form;
        const formItemLayout = {
            labelCol: {span: 6},
            wrapperCol: {span: 18},
        };

        let children = [];
        for (let f of fields) {
            let field = new Field(f);
            if (field.displayFlag && field.name != "objectRrn") {
                children.push(<Col span={12} key={field.objectRrn}>
                    {field.buildFormItem(getFieldDecorator, formItemLayout, true)}
                </Col>);
            }
        }
        return children;
    }

    // buildTabs = () => {
    //     const tabs = this.props.tabs;
    //     const fileds = this.props.fields;
    //     const children = [];
    //     if (tabs == null || tabs == undefined) {
    //         children.push(Tab.buildBasicTabPanel(fileds));
    //     } else {
    //         //TODO 处理TABS的生成。需考虑生成form还是生成table。
    //     }
    //     return (<Tabs>
    //         {children};
    //     </Tabs>);
    // }
    buildBasicTab =() =>  {
        const {getFieldDecorator} = this.props.form;
        return (<Tabs defaultActiveKey="Basic">
                    <TabPane tab="基本信息" key="Basic">
                        <Form>
                            {getFieldDecorator('objectRrn')(
                                <Input type='hidden'/>
                            )}
                            <Row gutter={16}>
                                {this.buildFields()}
                            </Row>
                        </Form>
                    </TabPane>
            </Tabs>)
    }

    render() {
        return (
            <div>
                <Modal width={740} title="编辑" object={this.props.object} visible={this.props.visible} confirmLoading={this.props.confirmLoading}
                    onOk={this.props.onOk} onCancel={this.props.onCancel}>
                    {this.buildBasicTab()}
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

// const WrappedAdvancedEntityForm = Form.create()(EntityForm);
// export default WrappedAdvancedEntityForm;
