import React, { Component } from 'react';
import { Form, Row, Col, Button, Icon } from 'antd';
import './QueryForm.scss';

import TableManagerRequestBody from '../../api/table-manager/TableManagerRequestBody';
import TableManagerRequestHeader from '../../api/table-manager/TableManagerRequestHeader';
import Request from '../../api/Request';
import {UrlConstant} from "../../api/const/ConstDefine";
import MessageUtils from '../../api/utils/MessageUtils';
import Field from '../../api/dto/ui/Field';
import * as PropTypes from 'prop-types';

const FormItem = Form.Item;

export default class QueryForm extends Component {
    static displayName = 'QueryForm';
    
    constructor(props) {
        super(props);
        let tableRrn = this.props.tableRrn;
        this.state = {
            expand: false,
            tableRrn: tableRrn,
            queryFields: []
        };
    }
    
    componentDidMount() {
        this.getQueryFields(this.state.tableRrn);
    }

    getQueryFields = (tableRrn) => {
        let self = this;
        let requestBody = TableManagerRequestBody.buildGetByRrn(tableRrn);
        let requestHeader = new TableManagerRequestHeader();
        let request = new Request(requestHeader, requestBody, UrlConstant.TableMangerUrl);
        let requestObject = {
            request: request,
            success: function(responseBody) {
                let fields = responseBody.table.fields;
                let queryFields = [];
                for (let field of fields) {
                    let f = new Field(field);
                    if (f.isQueryField()) {
                        queryFields.push(f);
                    }
                }
                self.setState({queryFields: queryFields})
            }
        }
        
        MessageUtils.sendRequest(requestObject);
    }

    handleSearch = (e) => {
        e.preventDefault();
        this.props.form.validateFields((err, values) => {
          console.log('Received values of form: ', values);
        });
    }
    
    handleReset = () => {
        this.props.form.resetFields();
    }
    
    toggle = () => {
        const { expand } = this.state;
        this.setState({ expand: !expand });
    }

    getFormItem = (field) => {
        const { getFieldDecorator } = this.props.form;
        let rules = field.buildRule();
        return <FormItem hasFeedback label={field.title}>
          {getFieldDecorator(field.name, {
            rules: rules,
          })
          (
            field.buildControl()
          )}
        </FormItem>
    }
    
    getFields = (queryFields) => {
        const count = this.state.expand ? 10 : 3;
        const children = [];
        let colSpan = queryFields.length >= 3 ? 8 : 24 / queryFields.length;
        for (let i in queryFields) {
          let field = queryFields[i];
          children.push(
            <Col span={colSpan} key={i} style={{ display: i < count ? 'block' : 'none' }}>
              {this.getFormItem(field)}
            </Col>
          );
        }
        return children;
    }

    render() {
        return (
        <div style={styles.tableFilter}>
            <Form className="ant-advanced-search-form" onSubmit={this.handleSearch}>
            <Row >
                <Row gutter={24}>{this.getFields(this.state.queryFields)}</Row>
                <Col span={24} style={{ textAlign: 'right' }}>
                    <Button type="primary" htmlType="submit">查找</Button>
                    <Button style={{ marginLeft: 8 }} onClick={this.handleReset}>
                    重置
                    </Button>
                    <a style={{ marginLeft: 8, fontSize: 12 }} onClick={this.toggle}>
                    展开 <Icon type={this.state.expand ? 'up' : 'down'} />
                    </a>
                </Col>
            </Row>

            </Form>
        </div>
        );
    }
}

QueryForm.prototypes = {
    tableRrn: PropTypes.number.isRequired
}

const styles = {
    tableFilter: {
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between',
      padding: '20px',
      marginBottom: '20px',
      background: '#fff',
    }
};