import React, { Component } from 'react';
import { Select } from 'antd';

const { Option} = Select;

export default class Combox extends Component {

    static displayName = 'Combox';

    constructor(props) {
        super(props);
        let value = "";
        if (props.value && props.value.toString().length > 0) {
            value = props.value.toString();
        }
        this.state = {
            data: [],
            value: value,
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

    filterOption = (inputValue, option) => {
        if (option.props.children.startsWith(inputValue)) {
            return true;
        }
        return false;
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

    }

    render() {
        const {data, value} = this.state;
        const options = data.map(data => <Option key={data.key}>{data.value}</Option>);
        return (
          <Select
            showSearch
            defaultValue={value}
            placeholder={this.props.placeholder}
            style={this.props.style ? this.props.style : { width: "150px" }}
            onChange={this.handleChange}
            disabled={this.props.disabled}
            filterOption={this.filterOption}
          >
            {options}
          </Select>
        );
    }

}
