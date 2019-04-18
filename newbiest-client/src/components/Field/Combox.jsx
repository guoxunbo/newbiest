import React, { Component } from 'react';
import { Select } from 'antd';
import EventUtils from '../../api/utils/EventUtils';

const { Option} = Select;

/**
 * 重新封装select。用于适用当前需求。 改变值来源只需重写combox即可
 * 因为重新封装了select。故通过getFieldDecorator的InitialValue无法赋予初始值。
 * 故此处用props.initialValue进行赋值
 */
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
            value: value
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
    }

    componentWillReceiveProps(nextProps) {
        if ('value' in nextProps && nextProps.value && this.state.value !== nextProps.value) {
            this.setState({
              value: nextProps.value
            });
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
        if (this.props.onChange) {
            this.props.onChange(changedValue);
        }
        this.notifyValueChanged(changedValue);
    }

    /**
     * 当值发生变化的时候出发，用于二级联动
     * @param sender 发送者 即谁触发了这个事件
     * @param value 触发值
     */
    valueChanged = (sender, value) => {
    }

    /**
     * 当值发生变化的时候通知
     */
    notifyValueChanged = (changedValue) => {
        // 发送事件变化
        EventUtils.getEventEmitter().emit(EventUtils.getEventNames.ComboxValueChanged, this, changedValue);
    }

    /**
     * 具体的加载由子类实现
     */
    queryData = (parameters) => {
        
    }

    render() {
        const {data} = this.state;
        const options = data.map(d => <Option key={d.key}>{d.value}</Option>);
        return (
          <Select
            showSearch
            allowClear
            defaultValue={this.state.value}
            placeholder={this.props.placeholder}
            style={this.props.style ? this.props.style : { width: '100%'}}
            onChange={this.handleChange}
            disabled={this.props.disabled}
            filterOption={this.filterOption}
          >
            {options}
          </Select>
        );
    }

}
