import React, { Component } from 'react';
import '../../properties.scss'

import TableManagerRequestBody from '../../../../api/table-manager/TableManagerRequestBody';
import TableManagerRequestHeader from '../../../../api/table-manager/TableManagerRequestHeader';
import Request from '../../../../api/Request';
import {UrlConstant} from '../../../../api/const/ConstDefine'
import MessageUtils from '../../../../api/utils/MessageUtils';
import EntityListTable from '../../../../components/Table/EntityListTable';
import WrappedAdvancedQueryForm from '../../../../components/Form/QueryForm';

export default class EntityProperties extends Component {
  static displayName = 'EntityProperties';

  constructor(props) {
    super(props);
    this.state = {
      tableRrn : this.props.match.params.tableRrn,
      tableData: undefined,
      table: {fields: []},
      loading: true
    };
  }

  componentDidMount =() => {
    this.getTableData();
  }

  getTableData = (whereClause) => {
    const self = this;
    let requestBody = TableManagerRequestBody.buildGetData(this.state.tableRrn, whereClause);
    let requestHeader = new TableManagerRequestHeader();
    let request = new Request(requestHeader, requestBody, UrlConstant.TableMangerUrl);
    let requestObject = {
      request: request,
      success: function(responseBody) {
        self.setState({
          tableData: responseBody.dataList,
          table: responseBody.table,
          loading: false
        });
      }
    }
    MessageUtils.sendRequest(requestObject);
  }
  
  handleSearch = (whereClause) => {
    this.setState({loading: true});
    this.getTableData(whereClause);
  }

  buildTable = () => {
      return  <EntityListTable table={this.state.table} data={this.state.tableData} loading={this.state.loading}/>
  }

  render() {
    return (
      <div className="list-table-manager-page">
        <div className="router-body">
          <WrappedAdvancedQueryForm tableRrn={this.state.tableRrn} onSearch={this.handleSearch.bind(this)} />
          {this.buildTable()}
        </div>
      </div>
    );
  }
}
