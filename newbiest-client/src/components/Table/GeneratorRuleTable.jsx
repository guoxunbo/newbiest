import EntityListTable from "./EntityListTable";
import { Form } from "antd";
import GeneratorRuleDrawer from "../drawer/GeneratorRuleDrawer";
import TableObject from '../../api/dto/ui/Table';

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

    onDrawerClose = () => {
        this.setState({
            drawerVisiable : false,
        })
    }

    createForm = () => {
        let childrens = [];
        const WrappedAdvancedGeneratorRuleDrawer = Form.create()(GeneratorRuleDrawer);
        childrens.push(<WrappedAdvancedGeneratorRuleDrawer table={this.state.table} object={this.state.editorObject} onDrawerClose={this.onDrawerClose} key="entityDrawer" visible={this.state.drawerVisiable}/>);
        return childrens;
    }


}