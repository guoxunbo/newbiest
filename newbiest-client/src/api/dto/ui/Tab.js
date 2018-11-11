import Field from './Field';
import {Form, Input, Row, Col, Tabs} from 'antd';
import {SessionContext} from '../../Application';
import {Language} from '../../const/ConstDefine';

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
    //前端栏位
    title;

    constructor(tab) {
        this.name = tab.name;
        this.description = tab.description;
        this.tableRrn = tab.tableRrn;
        this.seqNo = tab.seqNo;
        this.tabType = tab.tabType;
        this.fields = tab.fields;
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

    buildTab = (form, formLayout) => {
        const fields = this.fields;
        let children = [];
        if (TabType.Field == this.tabType) {
            for (let f of fields) {
                let field = new Field(f);
                if (!field.basicFlag && field.displayFlag && field.name != "objectRrn") {
                    children.push(<Col span={12} key={field.objectRrn}>
                        {field.buildFormItem(form, formLayout, true)}
                    </Col>);
                }
            }
        } else if (TabType.Table == this.tabType) {

        }
        return <TabPane tab={this.title} key={this.name}>
                    <Row gutter={16}>
                        {children}
                    </Row>
                </TabPane>
    }

}