import  React, { Component } from 'react';

import { Form, Input, Select, Modal, } from 'antd';

const FormItem = Form.Item;
const { Option } = Select;

/**
 * 修改密码
 */
const formItemLayout = {
    labelCol: {span: 4},
    wrapperCol: {span: 18},
};
class ChangePwdForm extends Component {

    static displayName = 'ChangePwdDialog';

    constructor(props) {
        super(props);
    }

    compareConfirmPassword = (rule, value, callback) => {
        const form = this.props.form;
        if (value) {
            form.validateFields(['confirmPassword'], { force: true });
        }
        callback();
    }

    compareNewPassword = (rule, value, callback) => {
        const form = this.props.form;
        if (value && value !== form.getFieldValue('newPassword')) {
          callback(I18NUtils.getClientMessage(i18NCode.TwoPwdIsNotInconsistent));
        } else {
          callback();
        }
    }

    handleSave = (values) => {
        
    }

    buildForm = () => {
        const {getFieldDecorator} = this.props.form;
        return (
            <Form>
                {/* <FormItem
                    {...formItemLayout}
                    label={"用户名"}>
                {getFieldDecorator('username', {initialValue: "admin" })(
                    <Input disabled/>
                )}
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label={"密码"}>
                {getFieldDecorator('password', {
                    rules: [{
                        required: true
                    }]
                })(
                    <Input type="password"/>
                )}
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label={"新密码"}>
                {getFieldDecorator('newPassword', {
                    rules: [{
                        required: true
                    }, {
                        validator: this.compareConfirmPassword,
                    }],
                })(
                    <Input type="password"/>
                )}
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label={"确认密码"}>
                {getFieldDecorator('confirmPassword', {
                    rules: [{
                        required: true
                    }, {
                        validator: this.compareNewPassword,
                    }],
                })(
                    <Input type="password"/>
                )}
                </FormItem> */}
                
                <Form.Item
          label="Select"
          hasFeedback
        >
          {getFieldDecorator('select', {
            rules: [
              { required: true, message: 'Please select your country!' },
            ],
          })(
            <Select placeholder="Please select a country">
              <Option value="china">China</Option>
              <Option value="usa">U.S.A</Option>
            </Select>
          )}
        </Form.Item>
            </Form>)
    }
    
    render() {
        return (
            <div>
                <Modal width={1040} centered title={"编辑"} 
                    object={this.props.object} visible={this.props.visible} 
                    maskClosable={false} onOk={this.handleOk} onCancel={this.props.onCancel} 
                    okText={"确定"}>
                    {this.buildForm()}
                </Modal>
            </div>
        );
    }
}
export default Form.create({ name: 'validate_other' })(ChangePwdForm);