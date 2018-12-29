import React, { Component } from 'react';
import UserLogin from './components/UserLogin';

export default class UI extends Component {
  static displayName = 'UI';

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
