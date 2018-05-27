import React, { PureComponent } from 'react';
import { Link } from 'react-router-dom';
import {Application} from '../js/Application';


export default class Logo extends PureComponent {
  render() {
    return (
      <div className="logo" style={{}}>
        <Link to="/" className="logo-text">
          <img src="../../public/images/logo.png"/>{'    '}
          {Application.name}
        </Link>
      </div>
    );
  }
}
