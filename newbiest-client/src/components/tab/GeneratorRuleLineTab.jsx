import {Component} from 'react';
import * as PropTypes from 'prop-types';
import { Tabs, Row, Input } from 'antd';
import GeneratorRuleLineTable from '../Table/GeneratorRuleLineTable';

const TabPane = Tabs.TabPane;

export default class GeneratorRuleLineTab extends Component {

    constructor(props) {
        super(props);
    }

    createLineTable = (tab) => {
        return <GeneratorRuleLineTable/>;
    }

    buildGeneratorRuleLineTab = (tab) => {
        return (<TabPane tab="aaa" key={tab.name}>
                    <Row gutter={16}>
                        {/* {this.createLineTable(tab)} */}
                    </Row>
            </TabPane>)
    }

    render() {
        console.log("12");
        return (<TabPane tab="aaa" >
                    <Row gutter={16}>
                        <Input></Input>
                        {/* {this.createLineTable(tab)} */}
                    </Row>
            </TabPane>);
    }
} 

GeneratorRuleLineTab.prototypes = {
    tabObject: PropTypes.object.isRequired
}
