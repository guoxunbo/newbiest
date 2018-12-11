import React, { Component } from 'react';
import { Select } from 'antd';
import EventUtils from '../../api/utils/EventUtils';

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

    componentWillUnmount = () => {
        this.setState = (state,callback)=>{
          return;
        };
    }

    componentDidMount() {
        this.queryData();
        // 监听值变化事件
        EventUtils.getEventEmitter().on(EventUtils.getEventNames.ComboxValueChanged, (sender, value) => {
            this.valueChanged(sender, value);
        });
        console.log(this._select);
    }

    componentWillReceiveProps(nextProps) {
        if ('value' in nextProps) {
            const value = nextProps.value;
            if (value != undefined && value != null) {
                this.setState({
                    value: value
                });
            }
        }
    }

    filterOption = (inputValue, option) => {
        if (option.props.value.startsWith(inputValue)) {
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
        console.log(this._select);
    }

    triggerChange = (changedValue) => {
        const onChange = this.props.onChange;
        if (onChange) {
            onChange(changedValue);
        }
        this.notifyValueChanged(changedValue);
    }

    /**
     * 当值发生变化的时候出发，用于二级联动
     * @param sender 发送者 即谁触发了这个事件
     * @param value 触发值
     */
    valueChanged = (sender, value) => {
        console.log(sender, value);
    }

    /**
     * 当值发生变化的时候通知
     */
    notifyValueChanged = (changedValue) => {
        // 发送事件变化
        EventUtils.getEventEmitter().emit(EventUtils.getEventNames.ComboxValueChanged, this, changedValue);
    }

    queryData = () => {

    }

    render() {
        const {data, value} = this.state;
        const options = data.map(d => <Option key={d.key}>{d.value}</Option>);
        return (
          <Select
            showSearch
            allowClear
            // value = {value}
            defaultValue={value}
            placeholder={this.props.placeholder}
            style={this.props.style ? this.props.style : { width: '100%'}}
            onChange={this.handleChange}
            disabled={this.props.disabled}
            // filterOption={this.filterOption}
            ref={(c) => this._select = c}
          >
            {options}
          </Select>
        );
    }

}
