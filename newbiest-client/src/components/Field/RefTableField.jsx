import React, { Component } from 'react';
import { Select } from 'antd';
import RefTableManagerRequestBody from '../../api/ref-table-manager/RefTableManagerRequestBody';
import RefTableManagerRequestHeader from '../../api/ref-table-manager/RefTableManagerRequestHeader';
import {UrlConstant} from "../../api/const/ConstDefine";
import MessageUtils from "../../api/utils/MessageUtils";

import Request from '../../api/Request';
const { Option} = Select;

export default class RefTableField extends Component {

    static displayName = 'RefTableField';

    constructor(props) {
        super(props);
        let value = "";
        if (props.value && props.value.toString().length > 0) {
            value = props.value.toString();
        }
        this.state = {
            refTableName: this.props.refTableName,
            data: [],
            value: value 
        };
    }

    componentDidMount() {
        this.queryData();
    }

    componentWillReceiveProps(nextProps) {
        if ('value' in nextProps) {
            const value = nextProps.value;
            if (value != undefined && value != null) {
                this.setState({
                    value: value.value
                });
            }
        }
    }

    handleChange = (currentValue) => {
        // 只有当值发生改变的时候才触发
        if (this.state.value === currentValue) {
            return;
        }
        this.setState({ 
            value: currentValue
        });
        this.triggerChange(currentValue);
    }

    triggerChange = (changedValue) => {
        const onChange = this.props.onChange;
        if (onChange) {
            onChange(changedValue);
        }
    }
    
    queryData = () => {
        let self = this;
        let requestBody = RefTableManagerRequestBody.buildGetData(this.state.refTableName)
        let requestHeader = new RefTableManagerRequestHeader();
        let request = new Request(requestHeader, requestBody, UrlConstant.RefTableManagerUrl);
        let requestObject = {
            request: request,
            success: function(responseBody) {
                let refTable = responseBody.referenceTable;
                let data = [];
                responseBody.dataList.map(d => {
                    let refData = {
                        key: d[refTable.keyField],
                        value: d[refTable.textField]
                    };
                    data.push(refData);
                }) 
                self.setState({data: data})
            }
        }
        MessageUtils.sendRequest(requestObject);
    }

    render() {
        const {data, value} = this.state;
        const options = data.map(data => <Option key={data.key.toString()}>{data.value}</Option>);
        return (
          <Select
            showSearch
            defaultValue={value}
            placeholder={this.props.placeholder}
            style={this.props.style ? this.props.style : { width: "150px" }}
            onChange={this.handleChange}
            disabled={this.props.disabled}
          >
            {options}
          </Select>
        );
    }

}