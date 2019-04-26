import React, { Component } from 'react';
import { Form, Row, Col, Button } from 'antd';
import './QueryForm.scss';
import {SqlType, DateFormatType} from "../../api/const/ConstDefine";
import Field from '../../api/dto/ui/Field';
import * as PropTypes from 'prop-types';

import StringBuffer from '../../api/StringBuffer';
import TableManagerRequest from '../../api/table-manager/TableManagerRequest';
import moment from 'moment';

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
        let requestObject = {
            tableRrn: tableRrn,
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
        TableManagerRequest.sendGetByRrnRequest(requestObject);
    }

    buildWhereClause = (formValues) => {
        const queryFields = this.state.queryFields;
        let whereClause = new StringBuffer();
        let firstFlag = true;
        for (let queryField of queryFields) {
            let fieldName = queryField.name;
            let fieldValue = formValues[fieldName];
            if (fieldValue && fieldValue != "") {
                if (!firstFlag) {
                    whereClause.append(SqlType.And);
                }
                whereClause.append(fieldName);
                // 如果是个数组。则需要用>= 以及<=了
                if (Array.isArray(fieldValue) && fieldValue.length == 2) {
                    whereClause.append(SqlType.Gt);
                    whereClause.append("'")
                    whereClause.append(fieldValue[0]);
                    whereClause.append("'")
                    whereClause.append(SqlType.And);
                    whereClause.append(fieldName);
                    whereClause.append(SqlType.Lt);
                    whereClause.append("'")
                    whereClause.append(fieldValue[1]);
                    whereClause.append("'")
                } else {
                    fieldValue = fieldValue.toString();
                    if (fieldValue.indexOf('*') != -1) {
                        whereClause.append(SqlType.Like);
                        //加/g表示全部替换
                        fieldValue = fieldValue.replace(/\*/g, '%');
                    } else {
                        whereClause.append(SqlType.Eq);
                    }
                    whereClause.append("'")
                    whereClause.append(fieldValue);
                    whereClause.append("'")
                }
                
               
                firstFlag = false;
            }
        }
        return whereClause.toString();
    }

    handleSearch = (e) => {
        e.preventDefault();
        console.log(moment);
        var self = this;
        this.props.form.validateFields((err, values) => {
            if (err) {
                return;
            }
            // 处理时间类型的栏位相关 antd的时间栏位类型是Moment，需要自己转换
            for (let property in values) {
                if (values[property]) {
                    debugger;
                    // 如果是单独的时间类型，不是个区域时间(dateFromTo)的话
                    if (moment.isMoment(values[property])) {
                        values[property] = values[property].format(DateFormatType.DateTime)
                    }
                    if (Array.isArray(values[property])) {
                        // 如果第一个栏位不是moment的话，则说明不是时间数组，则跳过
                        if (!moment.isMoment(values[property][0])) {
                            continue;
                        }
                        // 当前处理为0点0分0秒到23点59分59秒。即如果from 4号 to 4号。就是4号零点到4号23点59分59秒。
                        let fromDate = values[property][0].hour(0).minute(0).second(0);
                        let toDate = values[property][1].hour(23).minute(59).second(59);
                        values[property] = [fromDate.format(DateFormatType.DateTime), toDate.format(DateFormatType.DateTime)]
                    }
                }
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
        const children = [];
        let colSpan = queryFields.length >= 3 ? 8 : 24 / queryFields.length - 2 ;
        if(queryFields.length === 1) {
            colSpan = 16
        }
        for (let i in queryFields) {
          let field = queryFields[i];
          children.push(
            <Col span={colSpan} key={i}>
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
                    <Form className="ant-advanced-search-form" >
                        <Row gutter={24} style={{display:'inline-block'}}>{queryFields}
                        {queryFields.length > 1 ? <Button type="primary" className="search-button ant-col-3" onClick={this.handleSearch}>查找</Button> :
                        <Button type="primary" className="search-button ant-col-6" onClick={this.handleSearch}>查找</Button> }
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