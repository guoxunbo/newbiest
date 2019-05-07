/* eslint react/no-string-refs:0 */
import React, { Component } from 'react';
import { withRouter } from 'react-router-dom';
import './UserLogin.scss';
import {Application, SessionContext} from '../../../../api/Application';
import Authority from '../../../../api/dto/ui/Authority';
import UserLoginForm from '../../../../components/Form/UserLoginForm';
import I18NUtils from '../../../../api/utils/I18NUtils';
import { i18NCode } from '../../../../api/const/i18n';

// 寻找背景图片可以从 https://unsplash.com/ 寻找
const backgroundImage = require('./images/background.png');

@withRouter
export default class UserLogin extends Component {
  static displayName = 'UserLogin';

  static propTypes = {};

  static defaultProps = {};

  constructor(props) {
    super(props);
  }

  handleLogined = (responseBody, org, language) => {
    let user = responseBody.user;
    SessionContext.saveSessionContext(user.username, user.description, org, language, user.token, Authority.buildMenu(user.authorities, language));
    this.props.history.push('/Home');
  };

  render() {
    return (
      <div style={styles.userLogin} className="user-login">
        <div
          style={{
            ...styles.userLoginBg,
            backgroundImage: `url(${backgroundImage})`,
          }}
        />
        <img src></img>
        <div style={styles.contentWrapper} className="content-wrapper">
          <h2 style={styles.slogan} className="slogan">
          {I18NUtils.getClientMessage(i18NCode.WelcomeUse)} <br /> {Application.name} - {Application.version}
          </h2>
          <div style={styles.formContainer}>
            <h4 style={styles.formTitle}>{I18NUtils.getClientMessage(i18NCode.Login)}</h4>
            <UserLoginForm handleOk={this.handleLogined}></UserLoginForm>
          </div>
        </div>
      </div>
    );
  }
}

const styles = {
  userLogin: {
    position: 'relative',
    height: '100vh',
  },
  userLoginBg: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    backgroundSize: 'cover',
  },
  formContainer: {
    display: 'flex',
    justifyContent: 'center',
    flexDirection: 'column',
    padding: '30px 40px',
    background: '#fff',
    borderRadius: '6px',
    boxShadow: '1px 1px 2px #eee',
  },
  slogan: {
    color: "#3080fe"
  },
  formTitle: {
    margin: '0 0 20px',
    textAlign: 'center',
    color: '#3080fe',
    letterSpacing: '12px',
  },
};
