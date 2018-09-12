import React, { Component } from 'react';
import SimpleTable from './components/SimpleTable';
import EntityListTable from '../../components/Table/EntityListTable'

export default class ListTableManager extends Component {
  static displayName = 'ListTableManager';

  constructor(props) {
    super(props);
    console.log(this.props.match.params.tableRrn);
    this.state = {
      tableRrn : this.props.match.params.tableRrn
    };
  }

  render() {
    return (
      <div className="list-table-manager-page">
        <EntityListTable tableRrn={this.state.tableRrn} check/>
      </div>
    );
  }
}
