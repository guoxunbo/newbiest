import React, { Component } from 'react';
import { Select } from 'antd';
import RefListManagerRequestBody from '../../api/ref-list-manager/RefListManagerRequestBody';
import RefListManagerRequestHeader from '../../api/ref-list-manager/RefListManagerRequestHeader';
import {UrlConstant} from "../../api/const/ConstDefine";
import MessageUtils from "../../api/utils/MessageUtils";

import Request from '../../api/Request';

const { Option} = Select;

export default class RefListField extends Component {

    static displayName = 'RefListField';

    constructor(props) {
        super(props);
        const value = "";
        this.state = {
            owner: this.props.owner,
            referenceName: this.props.referenceName,
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
            this.setState({
                value: value.value
            });
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
        let requestBody = {};
        if (this.state.owner) {
            requestBody = RefListManagerRequestBody.buildOwnerData(this.state.referenceName)
        } else {
            requestBody = RefListManagerRequestBody.buildSystemData(this.state.referenceName)
        }
        let requestHeader = new RefListManagerRequestHeader();
        let request = new Request(requestHeader, requestBody, UrlConstant.RefListMangerUrl);
        let requestObject = {
            request: request,
            success: function(responseBody) {
                self.setState({data: responseBody.dataList})
            }
        }
        MessageUtils.sendRequest(requestObject);
    }

    render() {
        const options = this.state.data.map(data => <Option key={data.key}>{data.value}</Option>);
        return (
          <Select
            showSearch
            placeholder={this.props.placeholder}
            style={this.props.style ? this.props.style : { width: "150px" }}
            onChange={this.handleChange}
            // defaultValue={options == null ? "" : options[0].key}
          >
            {options}
          </Select>
        );
    }
}