import React, { PureComponent } from 'react';
import Layout from '@icedesign/layout';
import cx from 'classnames';
import Logo from './logo/components/Logo';
import {Application} from '../api/Application'

export default class Footer extends PureComponent {
  render() {
    const { className, style } = this.props;
    return (
      <Layout.Footer
        className={cx('ice-design-layout-footer', className)}
        style={{
          ...style,
          lineHeight: '36px',
        }}
      >
        <div className="ice-design-layout-footer-body">
          <div style={{ filter: 'grayscale(100%)', opacity: 0.3 }}>
            <Logo />
          </div>
          <div className="copyright">
            {Application.version} 
            <a
              href={Application.copyright.url}
              target="_blank"
              className="copyright-link"
              rel="noopener noreferrer"
            >
              {Application.copyright.name} 
            </a>
          </div>
        </div>
      </Layout.Footer>
    );
  }
}
