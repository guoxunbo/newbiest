import { Input, InputNumber, DatePicker, Switch,Form, Tag } from 'antd';
import {SessionContext} from '../../Application'
import {Language, DateFormatType} from "../../const/ConstDefine";
import RefListField from '../../../components/Field/RefListField';
import RefTableField from '../../../components/Field/RefTableField';
import {Icon} from 'antd';
import locale from 'antd/lib/date-picker/locale/zh_CN';
import PropertyUtils from '../../utils/PropertyUtils';
import I18NUtils from '../../utils/I18NUtils';
import { i18NCode } from '../../const/i18n';

import moment from 'moment';

const { RangePicker} = DatePicker;
const FormItem = Form.Item;

const DisplayType = {
    text : "text",
    password : "password",
    int: "int",
    double: "double",
    //日期相关
    calendar : "calendar",
    calendarFromTo : "calendarFromTo",
    datetime : "datetime",
    datetimeFromTo: "datetimeFromTo",
    //选择框
    sysRefList: "sysRefList",
    userRefList: "userRefList",
    referenceTable: "referenceTable",
    //单选
    radio: "radio"
}

const NumberType = [DisplayType.int, DisplayType.double];
const DateType = [DisplayType.calendar, DisplayType.calendarFromTo, DisplayType.datetime, DisplayType.datetimeFromTo]

const DisplaySelectType = [DisplayType.sysRefList, DisplayType.userRefList, DisplayType.referenceTable];

const Aligin = {
    left : "left",
    right : "right",
    center: "center"
}

const DisplayLength = {
    min: 100,
    max: 400
}
export default class Field {

    displayFlag;
    queryFlag;
    mainFlag;
    basicFlag;
    fromParent;
    upperFlag;
    negativeFlag;

    objectRrn;
    name;
    label;
    labelZh;    
    labelRes;
    displayType;
    refListName;
    refTableName;
    defaultValue;
    tabRrn;
    displayLength;
    referenceRule;

    //验证栏位
    readonlyFlag;
    editable;
    requiredFlag;
    namingRule;
    queryRequireFlag;

    // 前端栏位
    title;
    placeHolder;
    disabled;
    form;
    width;
    minValue;
    style;

    /**
     * 构造方法
     * @param field 后台NBField类对应的实例化对象
     * @param form form表单 
     */
    constructor(field, form) {
        PropertyUtils.copyProperties(field, this);
        this.form = form;
        this.build();
    }

    build = () => {
        // 处理国际化
        let language = SessionContext.getLanguage();
        if (language == Language.Chinese) {
            this.title = this.labelZh;
        } else if (language == Language.English) {
            this.title = this.label;
        } else {
            this.title = this.labelRes;
        }
        // 处理长度
        this.width = this.displayLength;
        if (this.width < DisplayLength.min) {
            this.width = DisplayLength.min;
        } else if (this.width >= DisplayLength.max) {
            this.width = DisplayLength.max;
        }
        // 处理最小值
        if (this.negativeFlag) {
            this.minValue = undefined;
        } else {
            this.minValue = 0;
        }
    }

    //TODO 处理fixed和sorter
    buildColumn() {
        if (this.displayFlag && this.mainFlag) {
            // 文本靠左 数字靠右
            let aligin = Aligin.left;
            if (DisplayType.number == this.displayType) {
                aligin = Aligin.right;
            }
            let columnRender;
            // Table对布尔类型的数据会不显示。'true'会显示
            if (DisplayType.radio == this.displayType) {
                aligin = Aligin.center;
                columnRender = columnValue => (
                    <span>
                        <Tag color={columnValue ? 'green' : 'red'} >{columnValue ? I18NUtils.getClientMessage(i18NCode.Yes)
                                                                                : I18NUtils.getClientMessage(i18NCode.No)}</Tag>
                    </span>
                  )
            }
            let column = {
                key: this.name,
                title: this.title,
                dataIndex: this.name,
                align: aligin,
                width: this.width,
                render: columnRender
                // fixed: 'left',
                // sorter: (a, b) => a.id - b.id
            }
            return column;
        }
        return null;
    }

    isQueryField = () => {
        if (this.displayFlag && this.queryFlag) {
            return true;
        }
        return false;
    }

    buildStyle = (query) => {
        
    }
    /**
     * 根据不同的DisplayType创建不同的组件
     *  因为refList refTable是对select重新封装。故此处需要自己初始化值
     * @param edit 是否是编辑form 编辑form会处理editable栏位
     * @param query 是否是queryForm 如果是queryForm需要改变combox的宽度。
     * //TODO 处理默认时间今天，以及默认时间为最后一个月
     */
    buildControl(edit, query, initialValue) {
        this.buildDisabled(edit, query);
        if (this.displayType == DisplayType.text) {
            return <Input placeholder = {this.placeHolder} style={this.upperFlag ? styles.textUppercaseStyle : undefined} disabled={this.disabled}/>;
        } else if (this.displayType == DisplayType.int) {
            return <InputNumber min={this.minValue} disabled={this.disabled}/>;
        } else if (this.displayType == DisplayType.double) {
            return <InputNumber min={this.minValue} step={0.01} disabled={this.disabled}/>;
        } else if (this.displayType == DisplayType.password) {
            return <Input placeholder = {this.placeHolder} type="password" disabled={this.disabled}/>;
        } else if (this.displayType == DisplayType.calendar) {
            return <DatePicker locale={locale} disabled={this.disabled}/>
        } else if (this.displayType == DisplayType.calendarFromTo) {
            return <RangePicker locale={locale} disabled={this.disabled}/>
        } else if (this.displayType == DisplayType.datetime) {
            return <DatePicker locale={locale} showTime format={DateFormatType.DateTime} disabled={this.disabled} />
        } else if (this.displayType == DisplayType.datetimeFromTo) {
            return <RangePicker locale={locale} showTime format={DateFormatType.DateTime} disabled={this.disabled}/>
        } else if (this.displayType == DisplayType.sysRefList) {
            return <RefListField initialValue={initialValue} field={this} referenceName={this.refListName} style={query ? styles.queryComboxStyle: undefined} disabled={this.disabled}/>
        } else if (this.displayType == DisplayType.userRefList) {
            return <RefListField initialValue={initialValue} field={this} referenceName={this.refListName} owner style={query ? styles.queryComboxStyle: undefined} disabled={this.disabled}/>
        } else if (this.displayType == DisplayType.referenceTable) {
            return <RefTableField initialValue={initialValue} field={this} form={this.form} style={query ? styles.queryComboxStyle: undefined} disabled={this.disabled}/>
        } else if (this.displayType == DisplayType.radio) {
            return <Switch checkedChildren={<Icon type="check" />} unCheckedChildren={<Icon type="close" />} disabled={this.disabled}/>
        }
    }
    
    /**
     * 根据field类型不同创建不同的组件 以及规则
     * @param fieldDecorator form
     * @param formItemProperties form属性比如样式等
     * @param edit 是否是编辑form 编辑form会处理editable栏位
     * @param query 是否是queryForm queryForm的是否必输根据queryRequireFlag决定
     * @param initialValue 初始值
     */
    buildFormItem = (formItemProperties, edit, query, initialValue) => {
        //处理formItemPorperties TODO暂时不支持file上传组件检验
        if (!formItemProperties) {
            formItemProperties = {};
        } 
        const { getFieldDecorator } = this.form;
        let valuePropName = "value";
        if (this.displayType == DisplayType.radio) {
            valuePropName = "checked";
        } 
        let rules = this.buildRule(query);
        if (DateType.includes(this.displayType) && initialValue) {
            let formatCode = DateFormatType.Date;
            if (DisplayType.datetime === this.displayType) {
                formatCode = DateFormatType.DateTime;
            }
            initialValue = moment(initialValue, formatCode)
        }
        return (<FormItem {...formItemProperties} label={this.title}>

            {getFieldDecorator(this.name, {
                rules: rules,
                initialValue: initialValue,
                valuePropName: valuePropName,
            })
          (
            this.buildControl(edit, query, initialValue)
          )}
        </FormItem>);
    }

    /**
     * 创建table里面的foritem 不具备显示label功能
     * @param record 记录
     * @param 表格
     */
    buildTableFormItem = (record, form) => {
        let valuePropName = "value";
        if (this.displayType == DisplayType.radio) {
            valuePropName = "checked";
        } 
        let formValue = form ? form : this.form;
        const { getFieldDecorator } = formValue;
        let rules = this.buildRule(false);
        return (<FormItem>
            {getFieldDecorator(this.name, {
                rules: rules,
                valuePropName: valuePropName,
                initialValue: record[this.name]
            })
          (
            this.buildControl(true)
          )}
        </FormItem>);
    }


    buildDisabled = (edit, query) => {
        if (this.readonlyFlag && !query) {
            this.disabled = true;
            this.placeHolder = "";
        }
        // 当进行编辑(修改)对象的时候，判断其栏位是否是可编辑
        if (edit && !this.editable) {
            this.disabled = true;
            this.placeHolder = "";
        }
    }
    /**
     * 根据nbfield创建不同的规则rule
     *  只有当displayType为text才去检验规则，其他只处理是否只读
     * @param 是否是查询form生成。如果是则根据queryRequired来生成必输
     */
    buildRule(query) {
        let rules = [];
        let rule = {};
        rule.whitespace = true;
        if (this.requiredFlag) {
            rule.required = true;
        }
        if (query) {
            if (!this.queryRequireFlag) {
                rule.required = false;
            }
        }
        
        if (DisplayType.text == this.displayType) {
            // 只有当text的时候才支持正则
            if (this.namingRule) {
                rule.pattern = this.namingRule;
            }
        }

        if (DisplaySelectType.includes(this.displayType)) {
            rule.transform = (value) => {
                if (value) {
                    return value.toString();
                }
            }
        }

        if (DisplayType.radio == this.displayType) {
            rule.type = "boolean";
        }

         // 数字
        if (NumberType.includes(this.displayType)) {
            rule.type = "number";
            rule.transform = (value) => {
                if(value){
                    return Number(value);
                  }
            }
        }
        
        if (DateType.includes(this.displayType)) {
            rule.type = "object";
            if (this.displayType.endsWith("FromTo")) {
                rule.type = "array";
            }
        }
        rules.push(rule);
        return rules;
    }
}

const styles = {
    queryComboxStyle: {
        width: '160px'
    },
    textUppercaseStyle: {
        textTransform:"uppercase"
    }
};
export {DisplayType}