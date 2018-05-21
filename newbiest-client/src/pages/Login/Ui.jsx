import React, { Component } from 'react';
import UserLogin from './components/UserLogin';

export default class Ui extends Component {
  static displayName = 'Ui';

  constructor(props) {
    super(props);
    this.state = {};
  }

  render() {
    return (
      <div className="ui-page">
        <UserLogin />
      </div>
    );
  }
}
