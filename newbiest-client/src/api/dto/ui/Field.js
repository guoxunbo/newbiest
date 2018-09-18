import { Input, DatePicker, Select } from 'antd';

import {SessionContext} from '../../Application'
import {Language} from "../../const/ConstDefine";
import RefListField from '../../../components/Field/RefListField';

const { RangePicker} = DatePicker;

const DisplayLength = {
    Min: 50
};

const DisplayType = {
    text : "text",
    password : "password",
    calendar : "calendar",
    calendarFromTo : "calendarFromTo",
    datetime : "datetime",
    datetimeFromTo: "datetimeFromTo",
    sysRefList: "sysRefList",
    userRefList: "userRefList"
}

const NumberType = ["int", "double"];
const Aligin = {
    left : "left",
    right : "right"
}

export default class Field {

    name;
    dataType;
    displayLength;
    displayFlag;
    mainFlag;
    label;
    labelZh;    
    labelRes;
    queryFlag;
    displayType;
    refListName;
    // 前端栏位
    title;

    constructor(field) {
        this.name = field.name;
        this.dataType = field.dataType;
        this.displayLength = field.displayLength;
        this.displayFlag = field.displayFlag;
        this.mainFlag = field.mainFlag;
        this.queryFlag = field.queryFlag;
        this.label = field.label;
        this.labelZh = field.labelZh;
        this.labelRes = field.labelRes;
        this.displayType = field.displayType;
        this.refListName = field.refListName;
    }

    //TODO 处理fixed和sorter
    buildColumn() {
        if (this.displayFlag && this.mainFlag) {
            // 文本靠左 数字靠右
            let aligin = Aligin.left;
            if (NumberType.includes(this.dataType)) {
                aligin = Aligin.right;
            }
            let column = {
                key: this.name,
                title: this.buildTitle(),
                dataIndex: this.name,
                align: aligin,
                width: this.displayLength < DisplayLength.Min ? DisplayLength.Min : this.displayLength,
                // fixed: 'left',
                // sorter: (a, b) => a.id - b.id
            }
            return column;
        }
        return null;
    }

    isQueryField() {
        if (this.displayFlag && this.mainFlag && this.queryFlag) {
            this.title = this.buildTitle();
            return true;
        }
        return false;
    }

    buildTitle() {
        let title;
        let language = SessionContext.getLanguage();
        if (language == Language.Chinese) {
            title = this.labelZh;
        } else if (language == Language.English) {
            title = this.label;
        } else {
            title = this.labelRes;
        }
        return title;
    }

    /**
     * 根据不同的DisplayType创建不同的组件
     * //TODO 处理默认时间今天，以及默认时间为最后一个月
     */
    buildControl() {
        if (this.displayType == DisplayType.text) {
            return <Input placeholder = {this.title} />;
        } else if (this.displayType == DisplayType.password) {
            return <Input placeholder = {this.title} type="password"/>;
        } else if (this.displayType == DisplayType.calendar) {
            return <DatePicker />
        } else if (this.displayType == DisplayType.calendarFromTo) {
            return <RangePicker />
        } else if (this.displayType == DisplayType.datetime) {
            return <DatePicker showTime format="YYYY-MM-DD HH:mm:ss"/>
        } else if (this.displayType == DisplayType.datetimeFromTo) {
            return <RangePicker showTime format="YYYY-MM-DD HH:mm:ss"/>
        } else if (this.displayType == DisplayType.sysRefList) {
            return <RefListField referenceName="NBLanguage"/>
        }
    }
}