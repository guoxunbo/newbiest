import React, { Component } from 'react';
import { Modal, Form, Input, Row, Col } from 'antd';
import * as PropTypes from 'prop-types';
const FormItem = Form.Item;
import Field from '../../api/dto/ui/Field';

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

    getFormItem = (field) => {
        const { getFieldDecorator } = this.props.form;
        const formItemLayout = {
            labelCol: {span: 6},
            wrapperCol: {span: 18},
        };
        let rules = field.buildRule();
        return (
        <FormItem {...formItemLayout} hasFeedback label={field.title}>
          {getFieldDecorator(field.name, {
            rules: rules,
          })
          (
            field.buildControl(true)
          )}
        </FormItem>)
    }

    buildFields = () => {
        let fields = this.props.fields;
        let children = [];
        for (let f of fields) {
            let field = new Field(f);
            if (field.displayFlag && field.name != "objectRrn") {
                children.push(<Col span={12} key={field.objectRrn}>
                    {this.getFormItem(field)}
                </Col>);
            }
        }
        return children;
    }

    render() {
        const {getFieldDecorator} = this.props.form;
        return (
            <div>
                <Modal width={740} title="编辑" visible={this.props.visible} confirmLoading={this.props.confirmLoading}
                    onOk={this.props.onOk} onCancel={this.props.onCancel}>
                    <Form>
                        {getFieldDecorator('objectRrn')(
                            <Input type='hidden'/>
                        )}
                        <Row gutter={16}>
                            {this.buildFields()}
                        </Row>
                        </Form>
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
    fields: PropTypes.array
}