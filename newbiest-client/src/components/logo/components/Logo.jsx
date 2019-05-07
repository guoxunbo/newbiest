import React, { PureComponent } from 'react';
import { Link } from 'react-router-dom';
import {Application} from '../../../api/Application';

export default class Logo extends PureComponent {
  render() {
    return (
      <div className="logo">
        <Link to="/" className="logo-text">
          <img src="./images/logo.png"/>{'    '}
          {Application.name}
        </Link>
      </div>
    );
  }
}
