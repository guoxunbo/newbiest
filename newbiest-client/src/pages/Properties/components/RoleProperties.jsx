import EntityProperties from "./entityProperties/EntityProperties";
import RoleTable from "../../../components/Table/RoleTable";

export default class RoleProperties extends EntityProperties{

    static displayName = 'RoleProperties';
    
    buildTable = () => {
        return <RoleTable table={this.state.table} data={this.state.tableData} loading={this.state.loading} />
    }

}