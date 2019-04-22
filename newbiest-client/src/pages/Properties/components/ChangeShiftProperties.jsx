import EntityProperties from "./entityProperties/EntityProperties";
import ChangeShiftTable from "../../../components/Table/ChangeShiftTable";

export default class ChangeShiftProperties extends EntityProperties{

    static displayName = 'ChangeShiftProperties';
    
    buildTable = () => {
        return <ChangeShiftTable table={this.state.table} data={this.state.tableData} loading={this.state.loading} />
    }

}