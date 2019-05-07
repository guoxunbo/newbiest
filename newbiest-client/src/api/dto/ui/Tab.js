import Field from './Field';
import { Row, Col, Tabs} from 'antd';
import {SessionContext} from '../../Application';
import {Language} from '../../const/ConstDefine';
import EditorTable from '../../../components/Table/edit/EditorTable'

const TabPane = Tabs.TabPane;

/**
 * Table的类型 栏位展示还是表格展示 表格展示需要制定refTableRrn
 */
const TabType = {
    Field: "Field",
    Table: "Table"
}

export default class Tab {
    name;
    description;
    tableRrn;
    seqNo;
    tabType;
    fields;
    
    labelZh;
    label;
    labelRes;

    //tabType是Table的时候
    refTableName;
    editFlag;
    whereClause;
    
    //前端栏位
    title;

    constructor(tab) {
        this.name = tab.name;
        this.description = tab.description;
        this.tableRrn = tab.tableRrn;
        this.seqNo = tab.seqNo;
        this.tabType = tab.tabType;
        this.fields = tab.fields;

        this.refTableName = tab.refTableName;
        this.editFlag = tab.editFlag;
        this.whereClause = tab.whereClause;
        this.title = this.buildTitle(tab);
    }

    buildTitle = (tab) => {
        let title;
        let language = SessionContext.getLanguage();
        if (language == Language.Chinese) {
            title = tab.labelZh;
        } else if (language == Language.English) {
            title = tab.label;
        } else {
            title = tab.labelRes;
        }
        return title;
    }

    buildTab = (form, formLayout, formObject) => {
        let children = [];
        if (TabType.Field == this.tabType) {
            const fields = this.fields;
            for (let f of fields) {
                let field = new Field(f, form);
                if (!field.basicFlag && field.displayFlag && field.name != "objectRrn") {
                    children.push(<Col span={12} key={field.objectRrn}>
                        {field.buildFormItem(formLayout, false, undefined, formObject[field.name])}
                    </Col>);
                }
            }
        } else if (TabType.Table == this.tabType) {
            let whereClause = " 1 != 1";
            if (this.whereClause) {
                whereClause = this.whereClause.format(formObject);
                console.log(whereClause);
            }
            // 如果是新增的话不显示TAB为Table的新增和保存按钮
            let newFlag = !formObject.objectRrn ? true : false;
            children.push(<EditorTable parentObject={formObject} editFlag={this.editFlag} newFlag={newFlag} refTableName={this.refTableName} whereClause={whereClause} key={this.name}></EditorTable>)
        }
        return <TabPane tab={this.title} key={this.name}>
                    <Row gutter={16}>
                        {children}
                    </Row>
                </TabPane>
    }

}