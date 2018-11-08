import React, { Component } from 'react';
import EntityListTable from '../../components/Table/EntityListTable';
import QueryForm from '../../components/Form/QueryForm';
import './ListTableManager.scss'

import {Form} from 'antd';

export default class ListTableManager extends Component {
  static displayName = 'ListTableManager';

  constructor(props) {
    super(props);
    this.state = {
      tableRrn : this.props.match.params.tableRrn
    };
  }

  render() {
    const WrappedAdvancedSearchForm = Form.create()(QueryForm);
    return (
      <div className="list-table-manager-page">
        <div className="router-body">
          <WrappedAdvancedSearchForm tableRrn={this.state.tableRrn} />
          <EntityListTable tableRrn={this.state.tableRrn} check/>
        </div>
      </div>
    );
  }
}
