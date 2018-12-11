import React, { Component } from 'react';
import { Form, Row, Col, Button } from 'antd';
import './QueryForm.scss';

import TableManagerRequestBody from '../../api/table-manager/TableManagerRequestBody';
import TableManagerRequestHeader from '../../api/table-manager/TableManagerRequestHeader';
import Request from '../../api/Request';
import {UrlConstant, SqlType} from "../../api/const/ConstDefine";
import MessageUtils from '../../api/utils/MessageUtils';
import Field from '../../api/dto/ui/Field';
import * as PropTypes from 'prop-types';

import StringBuffer from '../../api/StringBuffer';

class QueryForm extends Component {
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
                    let f = new Field(field, self.props.form);
                    if (f.isQueryField()) {
                        queryFields.push(f);
                    }
                }
                self.setState({queryFields: queryFields})
            }
        }
        
        MessageUtils.sendRequest(requestObject);
    }

    buildWhereClause = (formValues) => {
        const queryFields = this.state.queryFields;
        let whereClause = new StringBuffer();
        let firstFlag = true;
        for (let queryField of queryFields) {
            let fieldName = queryField.name;
            let fieldValue = formValues[fieldName];
            if (fieldValue != null && fieldValue != undefined && fieldValue != "") {
                if (!firstFlag) {
                    whereClause.append(SqlType.And);
                }
                whereClause.append(fieldName);
                fieldValue = fieldValue.toString();
                //加/g表示全部替换
                if (fieldValue.indexOf('*') != -1) {
                    whereClause.append(SqlType.Like);
                    fieldValue = fieldValue.replace(/\*/g, '%');
                } else {
                    whereClause.append(SqlType.Eq);
                }
                whereClause.append("'")
                whereClause.append(fieldValue);
                whereClause.append("'")
                firstFlag = false;
            }
        }
        return whereClause.toString();
    }

    handleSearch = (e) => {
        e.preventDefault();
        var self = this;
        this.props.form.validateFields((err, values) => {
            if (err) {
                return;
            }
            let whereClause = self.buildWhereClause(values);
            if (self.props.onSearch) {
                self.props.onSearch(whereClause);
            } 
        });
    }
    
    handleReset = () => {
        this.props.form.resetFields();
    }

    getFields = (queryFields) => {
        const count = 10;
        const children = [];
        // const { getFieldDecorator } = this.props.form;
        let colSpan = queryFields.length >= 3 ? 8 : 24 / queryFields.length - 2 ;
        if(queryFields.length === 1){
            colSpan = 16
        }
        for (let i in queryFields) {
          let field = queryFields[i];
          children.push(
            <Col span={colSpan} key={i} style={{ display: i < count ? 'block' : 'none' }}>
              {field.buildFormItem(undefined, false, true)}
            </Col>
          );
        }
        return children;
    }

    render() {
        const queryFields = this.getFields(this.state.queryFields)
        if (Array.isArray(queryFields) && queryFields.length > 0) {
            return (
                <div style={styles.tableFilter}>
                   
                    <Form className="ant-advanced-search-form" onSubmit={this.handleSearch}>
                        <Row gutter={24} style={{display:'inline-block'}}>{queryFields}
                        {queryFields.length > 1 ?<Button type="primary" className="search-button ant-col-3" htmlType="submit">查找</Button> :
                        <Button type="primary" className="search-button ant-col-6" htmlType="submit">查找</Button> }
                        </Row>
                    </Form>
                 
                </div>);
        } else {
            return <div></div>
        }
        
    }
}

QueryForm.prototypes = {
    tableRrn: PropTypes.number.isRequired
}

const WrappedAdvancedQueryForm = Form.create()(QueryForm);
export default WrappedAdvancedQueryForm;

const styles = {
    tableFilter: {
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between',
      padding: '20px',
      paddingBottom:'0px',
      background: '#fff',
    }
};