import EntityListTable from "./EntityListTable";
import { Form } from "antd";
import GeneratorRuleDrawer from "../drawer/GeneratorRuleDrawer";
import TableObject from '../../api/dto/ui/Table';
import MessageUtils from "../../api/utils/MessageUtils";

export default class GeneratorRuleTable extends EntityListTable {

    static displayName = 'GeneratorRuleTable';

    createButtonGroup = () => {
        let buttons = [];
        buttons.push(this.createAddButton());
        return buttons;
    }

    handleAdd = () => {
        this.setState({
            drawerVisiable : true,
            editorObject: TableObject.buildDefaultModel(this.state.table.fields)
        })
    }

    handleEdit = (record) => {
        this.setState({
            drawerVisiable : true,
            editorObject: record
        })
    }

    /**
     * 更新表格数据
     * @param responseData 数据如用户、
     */
    refresh = (responseData) => {
        var self = this;
        let datas = self.state.data;
        let dataIndex = -1;
        datas.map((data, index) => {
            if (data.objectRrn == responseData.objectRrn) {
                dataIndex = index;
            }
        });
        if (dataIndex > -1) {
            datas.splice(dataIndex, 1, responseData);
        } else {
            // 新增的就放在第一位
            datas.unshift(responseData);
        }
        self.setState({
            data: datas,
            drawerVisiable: false
        }) 
        MessageUtils.showOperationSuccess();
    }

    onDrawerClose = () => {
        this.setState({
            drawerVisiable : false,
        })
    }

    createForm = () => {
        let childrens = [];
        const WrappedAdvancedGeneratorRuleDrawer = Form.create()(GeneratorRuleDrawer);
        childrens.push(<WrappedAdvancedGeneratorRuleDrawer key={GeneratorRuleDrawer.displayName} onOk={this.refresh} table={this.state.table} object={this.state.editorObject} onDrawerClose={this.onDrawerClose} visible={this.state.drawerVisiable}/>);
        return childrens;
    }


}