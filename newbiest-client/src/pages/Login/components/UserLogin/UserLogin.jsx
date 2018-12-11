/* eslint react/no-string-refs:0 */
import React, { Component } from 'react';
import { withRouter } from 'react-router-dom';

import { Input, Button, Grid} from '@icedesign/base';

import {
  FormBinderWrapper as IceFormBinderWrapper,
  FormBinder as IceFormBinder,
  FormError as IceFormError,
} from '@icedesign/form-binder';

import IceIcon from '@icedesign/icon';
import './UserLogin.scss';

import {Application, SessionContext} from '../../../../api/Application';
import MessageUtils from '../../../../api/utils/MessageUtils';

import Request from '../../../../api/Request';
import UserManagerRequestHeader from "../../../../api/user-manager/UserManagerRequestHeader";
import UserManagerRequestBody from "../../../../api/user-manager/UserManagerRequestBody";
import {UrlConstant, SystemRefListName, RefTableName} from "../../../../api/const/ConstDefine";
import RefListField from '../../../../components/Field/RefListField';
import RefTableField from '../../../../components/Field/RefTableField';
import Authority from '../../../../api/dto/ui/Authority';

const { Row, Col } = Grid;

// 寻找背景图片可以从 https://unsplash.com/ 寻找
const backgroundImage =
  'https://img.alicdn.com/tfs/TB1zsNhXTtYBeNjy1XdXXXXyVXa-2252-1500.png';

@withRouter
export default class UserLogin extends Component {
  static displayName = 'UserLogin';

  static propTypes = {};

  static defaultProps = {};

  constructor(props) {
    super(props);
    this.state = {
      value: {
        account: undefined,
        password: undefined,
        language: undefined,
        org: undefined,
        checkbox: false,
      },
    };
  }

  formChange = (value) => {
    this.setState({
      value,
    });
  };

  handleSubmit = (e) => {
    this.refs.form.validateAll((errors, values) => {
      let self = this;
      if (errors) {
        return;
      }
      let requestBody = UserManagerRequestBody.buildLoginRequestBody(values.account, values.password);
      let requestHeader = new UserManagerRequestHeader();
      requestHeader.orgRrn = values.org;
      requestHeader.language = values.language;
      let request = new Request(requestHeader, requestBody, UrlConstant.UserManagerUrl);
      let requestObject = {
        request: request,
        success: function(responseBody) {
          let user = responseBody.user;
          SessionContext.saveSessionContext(values.account, values.org, values.language, user.token, Authority.buildMenu(user.authorities, values.language));
          self.props.history.push('/Home');
        }
      }
      MessageUtils.sendRequest(requestObject);
    });
  };

  render() {
    const orgField = {
      refTableName : RefTableName.NBOrg
    }
    return (
      <div style={styles.userLogin} className="user-login">
        <div
          style={{
            ...styles.userLoginBg,
            backgroundImage: `url(${backgroundImage})`,
          }}
        />
        <div style={styles.contentWrapper} className="content-wrapper">
          <h2 style={styles.slogan} className="slogan">
            欢迎使用 <br /> {Application.name} - {Application.version}
          </h2>
          <div style={styles.formContainer}>
            <h4 style={styles.formTitle}>登录</h4>
            <IceFormBinderWrapper
              value={this.state.value}
              onChange={this.formChange}
              ref="form"
            >
              <div style={styles.formItems}>
                <Row style={styles.formItem}>
                  <Col>
                    <IceIcon
                      type="person"
                      size="small"
                      style={styles.inputIcon}
                    />
                    <IceFormBinder name="account" required message="必填">
                      <Input maxLength={20} placeholder="会员名/邮箱/手机号" />
                    </IceFormBinder>
                  </Col>
                  <Col>
                    <IceFormError name="account" />
                  </Col>
                </Row>

                <Row style={styles.formItem}>
                  <Col>
                    <IceIcon
                      type="lock"
                      size="small"
                      style={styles.inputIcon}
                    />
                    <IceFormBinder name="password" required message="必填">
                      <Input htmlType="password" placeholder="密码" />
                    </IceFormBinder>
                  </Col>
                  <Col>
                    <IceFormError name="password" />
                  </Col>
                </Row>

                <Row style={styles.formItem}>
                  <Col>
                    <IceIcon
                      type="location"
                      size="medium"
                      style={styles.selectIcon}
                    />
                    <IceFormBinder name="org" required message="必填">
                      <RefTableField field = {orgField} style={styles.formSelect} placeholder="区域"/>
                    </IceFormBinder>
                  </Col>
                  <Col>
                    <IceFormError name="org" />
                  </Col>
                </Row>

                <Row style={styles.formItem}>
                  <Col>
                    <IceIcon
                      type="requ"
                      size="medium"
                      style={styles.selectIcon}
                    />
                    <IceFormBinder name="language" required message="必填">
                      <RefListField referenceName={SystemRefListName.Language} style={styles.formSelect} placeholder="语言"/>
                    </IceFormBinder>
                  </Col>
                  <Col>
                    <IceFormError name="language" />
                  </Col>
                </Row>

                {/* <Row style={styles.formItem}>
                  <Col>
                    <IceFormBinder name="checkbox">
                      <Checkbox style={styles.checkbox}>记住账号</Checkbox>
                    </IceFormBinder>
                  </Col>
                </Row> */}

                <Row style={styles.formItem}>
                  <Button type="primary" onClick={this.handleSubmit} style={styles.submitBtn}>
                    登 录
                  </Button>
                </Row>

                <Row className="tips" style={styles.tips}>
                  {/* <a href="/" style={styles.link}>
                    立即注册
                  </a> */}
                  {/* <span style={styles.line}>|</span> */}
                  <a href="/" style={styles.link}>
                    忘记密码
                  </a>
                </Row>
              </div>
            </IceFormBinderWrapper>
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
  formItem: {
    position: 'relative',
    marginBottom: '25px',
    flexDirection: 'column',
  },
  formSelect: {
    marginLeft: "20px",
    width: "80%"
  },
  formTitle: {
    margin: '0 0 20px',
    textAlign: 'center',
    color: '#3080fe',
    letterSpacing: '12px',
  },
  inputIcon: {
    position: 'absolute',
    left: '0px',
    top: '3px',
    color: '#999',
  },
  selectIcon: {
    position: 'absolute',
    left: '-4px',
    top: '5px',
    color: '#999',
  },

  submitBtn: {
    width: '240px',
    background: '#3080fe',
    borderRadius: '28px',
  },
  checkbox: {
    marginLeft: '5px',
  },
  tips: {
    textAlign: 'center',
  },
  link: {
    color: '#999',
    textDecoration: 'none',
    fontSize: '13px',
  },
  line: {
    color: '#dcd6d6',
    margin: '0 8px',
  },
};
