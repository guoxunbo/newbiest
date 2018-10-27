import Field from './Field';
import {Form, Input, Row, Col, Tabs} from 'antd';
import {SessionContext} from '../../Application';
import {Language} from '../../const/ConstDefine';

const TabPane = Tabs.TabPane;

export default class Tab {
    name;
    description;
    tableRrn;
    seqNo;
    tabType;
    fields;

    //前端栏位
    title;

    constructor(tab) {
        this.name = tab.name;
        this.description = tab.description;
        this.tableRrn = tab.tableRrn;
        this.seqNo = tab.seqNo;
        this.tabType = tab.tabType;
        this.fields = tab.fields;
        this.title = this.buildTitle();
    }

    buildTitle = () => {
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

    buildFields = (fields) => {
        const { getFieldDecorator } = this.props.form;
        const formItemLayout = {
            labelCol: {span: 6},
            wrapperCol: {span: 18},
        };

        let children = [];
        for (let f of fields) {
            let field = new Field(f);
            if (field.displayFlag && field.name != "objectRrn") {
                children.push(<Col span={12} key={field.objectRrn}>
                    {field.buildFormItem(getFieldDecorator, formItemLayout, true)}
                </Col>);
            }
        }
        return children;
    }

    buildFormPanel = () => {
        const {getFieldDecorator} = this.props.form;
        return (<TabPane tab={this.title} key={this.name}>
                    <Form>
                        {getFieldDecorator('objectRrn')(
                            <Input type='hidden'/>
                        )}
                        <Row gutter={16}>
                            {this.buildFields(this.fields)}
                        </Row>
                    </Form>
                </TabPane>)
    }
    /**
     * 所有页面都会有basicTab。用来保存没有指定tabRrn的Field
     */
    static buildBasicTabPanel = (fields) => {
        // 取出没有指定tab的所有栏位都属于BasicTab
        console.log(fields);
        const basicFields = fields.filter(field => field.tabRrn);   
        let tab = new Tab({
            name: "Basic",
            seqNo: 1,
            fields: basicFields,
            label: "BasicInfo",
            labelZh: "基本信息"
        });
        return tab.buildFormPanel(tab);
    }
}