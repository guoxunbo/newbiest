import React, { Component } from 'react';
import SimpleTable from './components/SimpleTable';

export default class ListTableManager extends Component {
  static displayName = 'ListTableManager';

  constructor(props) {
    super(props);
    this.state = {};
  }

  render() {
    return (
      <div className="list-table-manager-page">
        <SimpleTable />
      </div>
    );
  }
}
