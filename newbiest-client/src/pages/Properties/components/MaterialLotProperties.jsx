import EntityProperties from "./entityProperties/EntityProperties";
import MaterialLotTable from "../../../components/Table/MaterialLotTable";

export default class MaterialLotProperties extends EntityProperties{

    static displayName = 'MaterialStaMaterialPropertiestusModelProperties';
    
    buildTable = () => {
        return <MaterialLotTable table={this.state.table} data={this.state.tableData} loading={this.state.loading} />
    }

}