import React, { Component } from 'react';
import EntityListTable from '../../components/Table/EntityListTable';
import WrappedAdvancedQueryForm from '../../components/Form/QueryForm';

import TableManagerRequestBody from '../../api/table-manager/TableManagerRequestBody';
import TableManagerRequestHeader from '../../api/table-manager/TableManagerRequestHeader';
import Request from '../../api/Request';
import {UrlConstant} from '../../api/const/ConstDefine'
import MessageUtils from '../../api/utils/MessageUtils';

export default class ListTableManager extends Component {
  static displayName = 'ListTableManager';

  constructor(props) {
    super(props);
    this.state = {
      tableRrn : this.props.match.params.tableRrn,
      tableData: undefined,
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

  render() {
    return (
      <div className="list-table-manager-page">
        <WrappedAdvancedQueryForm tableRrn={this.state.tableRrn} onSearch={this.handleSearch.bind(this)} />
        <EntityListTable tableRrn={this.state.tableRrn} data={this.state.tableData} loading={this.state.loading}/>
      </div>
    );
  }
}
