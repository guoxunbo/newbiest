import  React from 'react';

import {Tabs, Row } from "antd";
import Tab from '../../api/dto/ui/Tab';
import GeneratorRuleLineTable from '../Table/GeneratorRuleLineTable';
import EntityDrawer from './EntityDrawer';

const TabPane = Tabs.TabPane;

export default class GeneratorRuleDrawer extends EntityDrawer {
    static displayName = 'GeneratorRuleDrawer';

    buildTabs = () => {
        const tabs = this.props.table.tabs;
        const tabPanels = [];
        const formItemLayout = {
            labelCol: {span: 6},
            wrapperCol: {span: 18},
        };
        if (Array.isArray(tabs)) {
            tabs.forEach((tabObject) => {
                let tabPanel = new Tab(tabObject);
                if (tabPanel.name === "GeneratorRuleInfo") {
                    tabPanels.push(this.buildGeneratorRuleLineTab(tabPanel));
                } else {
                    tabPanels.push(tabPanel.buildTab(this.props.form, formItemLayout, this.props.object));
                }
            }) 
        }
        return (<Tabs>
           {tabPanels}
        </Tabs>)
    }

    buildGeneratorRuleLineTab = (tab) => {
        return <TabPane tab={tab.title} key={tab.name}>
                    <Row gutter={16}>
                        <GeneratorRuleLineTable tab={tab} parentObject={this.props.object} table={this.state.table} data={this.state.tableData}/>
                    </Row>
            </TabPane>
    }

}