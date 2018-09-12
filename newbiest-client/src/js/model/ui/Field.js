import {SessionContext} from '../../Application'
import {Language} from "../../const/ConstDefine";

const DisplayLength = {
    Min: 50
};
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

    constructor(field) {
        this.name = field.name;
        this.dataType = field.dataType;
        this.displayLength = field.displayLength;
        this.displayFlag = field.displayFlag;
        this.mainFlag = field.mainFlag;
        this.label = field.label;
        this.labelZh = field.labelZh;
        this.labelRes = field.labelRes;
    }

    //TODO 还没加属性
    buildColumn() {
        if (this.displayFlag && this.mainFlag) {
            let title;
            let language = SessionContext.getLanguage();
            if (language == Language.Chinese) {
                title = this.labelZh;
            } else if (language == Language.English) {
                title = this.label;
            } else {
                title = this.labelRes;
            }
            // 文本靠左 数字靠右
            let aligin = Aligin.left;
            if (NumberType.includes(this.dataType)) {
                aligin = Aligin.right;
            }
            let column = {
                key: this.name,
                title: title,
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
}