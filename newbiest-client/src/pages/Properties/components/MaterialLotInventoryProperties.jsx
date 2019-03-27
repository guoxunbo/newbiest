import EntityProperties from "./entityProperties/EntityProperties";
import MaterialLotInventoryTable from "../../../components/Table/MaterialLotInventoryTable";

export default class MaterialLotInventoryProperties extends EntityProperties{

    static displayName = 'MaterialStaMaterialPropertiestusModelProperties';
    
    buildTable = () => {
        return <MaterialLotInventoryTable table={this.state.table} data={this.state.tableData} loading={this.state.loading} />
    }

}