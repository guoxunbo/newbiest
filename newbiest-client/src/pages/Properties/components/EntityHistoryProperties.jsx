import EntityProperties from "./entityProperties/EntityProperties";
import EntityHistoryTable from "../../../components/Table/EntityHistoryTable";

export default class EntityHistoryProperties extends EntityProperties{

    static displayName = 'EntityHistoryProperties';
    
    buildTable = () => {
        return <EntityHistoryTable table={this.state.table} data={this.state.tableData} loading={this.state.loading} />
    }

}