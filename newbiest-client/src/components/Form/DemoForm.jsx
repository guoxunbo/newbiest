
import  React, { Component } from 'react';

import { Form, Input, Select, Modal, } from 'antd';

const FormItem = Form.Item;
const { Option } = Select;


class DemoForm extends Component {

    constructor(props) {
        super(props);
        this.state={
            value: ""
        }
    }

    componentWillReceiveProps(nextProps){
        console.log(nextProps);
    }

    handleChange = (currentValue) => {
        this.setState({value:currentValue});
        this.props.form.setFieldsValue({
            "select111": currentValue
        });
    }

    render() {
        const {getFieldDecorator} = this.props.form;
        
        return (
            <div>
                <Modal 
                    centered 
                    width={1040} 
                    title={"编辑"} 
                    visible={this.props.visible} 
                    maskClosable={false} 
                    onOk={this.handleOk}
                    onCancel={this.props.onCancel} 
                    okText={"确定"}>
                    
                    <Form>
                        <Form.Item
                            label="Select"
                            hasFeedback
                            >
                            <Select placeholder="Please select a country">
                                <Option value="china">China</Option>
                                <Option value="usa">U.S.A</Option>
                            </Select>
                            {getFieldDecorator('select111')(
                                <Select placeholder="Please select a country"
                                onChange={this.handleChange}>
                                <Option value="china">China</Option>
                                <Option value="usa">U.S.A</Option>
                                </Select>
                            )}
                        </Form.Item>
                    </Form>
                </Modal>
            </div>
        );
    }

}
export default Form.create({ name: 'validate_other' })(DemoForm);