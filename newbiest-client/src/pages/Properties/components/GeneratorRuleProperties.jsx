import EntityProperties from "./entityProperties/EntityProperties";
import GeneratorRuleTable from "../../../components/Table/GeneratorRuleTable";

export default class GeneratorRuleProperties extends EntityProperties{

    static displayName = 'GeneratorRuleProperties';
    
    buildTable = () => {
        return <GeneratorRuleTable table={this.state.table} data={this.state.tableData} loading={this.state.loading} />
    }

}