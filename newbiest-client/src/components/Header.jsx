import React, { PureComponent } from 'react';
import { withRouter } from 'react-router-dom';

import { Balloon, Icon } from '@icedesign/base';
import Layout from '@icedesign/layout';
import Menu from '@icedesign/menu';
import FoundationSymbol from 'foundation-symbol';
import cx from 'classnames';
import { Link } from 'react-router-dom';
import { headerMenuConfig } from './../menuConfig';
import Logo from './logo/components/Logo';
import {Notification} from './notice/Notice';
import {SessionContext} from '../api/Application';
import MessageUtils from '../api/utils/MessageUtils';
import ChangePwdForm from './Form/ChangePwdForm';
import { Avatar, Form } from 'antd';

@withRouter
export default class Header extends PureComponent {

  constructor(props) {
    super(props);
    this.state = {
        changePwdVisiable: false,
    };
  }

  logout= (e) => {
  }
  
  changePassword = (e) => {
    e.preventDefault();
    this.setState({changePwdVisiable: true})
  } 
  
  canelChangePwd = () => {
    this.setState({changePwdVisiable: false})
  }
  
  changePwdOk = () => {
    MessageUtils.showOperationSuccess();
    this.setState({changePwdVisiable: false})
  }
  
  render() {
    let sessionContext = SessionContext.getSessionContext();
    if (!sessionContext) {
      Notification.showInfo("请先登陆");
      this.props.history.push('/');
    }
    const { width, theme, isMobile, className, style } = this.props;
    const WrappedChangePwdForm = Form.create()(ChangePwdForm);
    return (
      <div>
      <Layout.Header
        theme={theme}
        className={cx('ice-design-layout-header', className)}
        style={{ ...style, width }}
      >
        <Logo />
        <div
          className="ice-design-layout-header-menu"
          style={{ display: 'flex' }}
        >
          {/* Header 菜单项 begin */}
          {headerMenuConfig && headerMenuConfig.length > 0 ? (
            <Menu mode="horizontal" selectedKeys={[]}>
              {headerMenuConfig.map((nav, idx) => {
                const linkProps = {};
                if (nav.newWindow) {
                  linkProps.href = nav.path;
                  linkProps.target = '_blank';
                } else if (nav.external) {
                  linkProps.href = nav.path;
                } else {
                  linkProps.to = nav.path;
                }
                return (
                  <Menu.Item key={idx}>
                    {linkProps.to ? (
                      <Link {...linkProps}>
                        {nav.icon ? (
                          <FoundationSymbol type={nav.icon} size="small" />
                        ) : null}
                        {!isMobile ? nav.name : null}
                      </Link>
                    ) : (
                      <a {...linkProps}>
                        {nav.icon ? (
                          <FoundationSymbol type={nav.icon} size="small" />
                        ) : null}
                        {!isMobile ? nav.name : null}
                      </a>
                    )}
                  </Menu.Item>
                );
              })}
            </Menu>
          ) : null}
          {/* Header 菜单项 end */}

          {/* Header 右侧内容块 */}

          <Balloon
            trigger={
              <div
                className="ice-design-header-userpannel"
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  fontSize: 12,
                }}
              >
                  <Avatar size="large" icon="user" className="user-avatar"/>
                 <div className="user-profile">
                  <span className="user-name" style={{ fontSize: '13px' }}>
                    {sessionContext ? sessionContext.username + ":" + sessionContext.description : ""}
                  </span>
                  <br />
                  <span
                    className="user-department"
                    style={{ fontSize: '12px', color: '#999' }}
                  >
                    {sessionContext ? sessionContext.orgName : ""}
                  </span>
                </div>
                <Icon
                  type="arrow-down-filling"
                  size="xxs"
                  className="icon-down"
                />
              </div>
            }
            closable={false}
            className="user-profile-menu"
          >
            <ul>
              <li className="user-profile-menu-item">
                <Link to="/">
                  <FoundationSymbol type="person" size="small" />我的主页
                </Link>
              </li>
              <li className="user-profile-menu-item">
                <Link to="/" onClick={this.changePassword}>
                  <FoundationSymbol type="lock" size="small" />修改密码
                </Link>
              </li>
              <li className="user-profile-menu-item">
                <Link to="/">
                  <FoundationSymbol type="repair" size="small" />设置
                </Link>
              </li>
              <li className="user-profile-menu-item">
                <Link to="/" onClick={this.logout}>
                  <FoundationSymbol type="compass" size="small" />退出
                </Link>
              </li>
            </ul>
          </Balloon>
        </div>
      </Layout.Header>
      <WrappedChangePwdForm object={{}} destroyOnClose onOk={this.changePwdOk} onCancel={this.canelChangePwd} visible={this.state.changePwdVisiable} />
      </div>
      
    );
  }
}
