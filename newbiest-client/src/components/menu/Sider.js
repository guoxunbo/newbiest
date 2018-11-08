import React from 'react'
import { connect } from 'react-redux'
import { NavLink } from 'react-router-dom'

import { Menu, Icon } from 'antd'

import * as mainAction from '../../redux/action/main'
const SubMenu = Menu.SubMenu
class SideMenu extends React.Component {
  constructor (props) {
    super(props)
    this.state = {
      theme: 'dark',
      openMenuKey: undefined,
      selectMenuKey: undefined,
      menuData: []
    }
  }

  handleClick = (event) => {
    this.props.onMenuChange(event.keyPath)
  }

  componentDidMount () {
    this.props.getMenu(() => {
      this.setMenuKey(null)
    })
  }

  componentWillReceiveProps (nextProps) {
    if (nextProps.current && (typeof nextProps.current === 'string') && this.props.current !== nextProps.current) {
      this.setMenuKey(null, nextProps)
    }
  }

  getPathText = (menuData) => {
    let pathText = []
    if (menuData instanceof Array) {
      let list = []
      menuData.map((item) => {
        this.getPathText(item)
      })
    } else {
      if (menuData.children && menuData.children.length > 0) {
        pathText.push(menuData.name)
        this.getPathText(menuData.children)
      } else {
        pathText.push(menuData.name)
      }
    }
    return pathText
  }

  openMenu = (keys) => {
    this.setMenuKey(keys[keys.length - 1] + '')
  }

  selectMenu = (item, key, selectedKeys) => {
    this.setState({
      selectMenuKey: item.key
    })
  }

  renderMenu (menuData, toSideMenus) {
    let vdom = []
    if (menuData instanceof Array) {
      let list = []
      menuData.map((item) => {
        list.push(this.renderMenu(item, toSideMenus))
      })
      vdom.push(list)
    } else {
      if (menuData.children && menuData.children.length > 0) {
        let child = []
        child.push(this.renderMenu(menuData.children, toSideMenus))
        let menuTypePath = menuData.children[0].path.split('/')
        let menuType = ''
        for (let i = 0; i < menuTypePath.length; i++) {
          if (menuTypePath[i]) {
            menuType = 'icon-mp-' + menuTypePath[i]
            break
          }
        }
        vdom.push(
          <SubMenu key={menuData.id} title={
            <span>
              <span>
                <i className={'icon iconfont ' + menuType}></i>
              </span>
              <span>{toSideMenus.indexOf(menuData.id) >= 0 ? menuData.name : menuData.name}</span>
            </span>
          }>
            {child}
          </SubMenu>
        )
      } else if (menuData.path) {
        vdom.push(<Menu.Item key={menuData.id}>
          {
            menuData.reference
              ? <NavLink to={menuData.path}>
                <span>
                  <span>
                    <i className={'icon iconfont icon-dot'}></i>
                  </span>
                  <span
                    title={menuData.name}>{ toSideMenus.indexOf(menuData.id) >= 0 ? menuData.name : menuData.name}</span>
                </span>
            </NavLink>
              : <NavLink to={menuData.path}>
                <span>
                  <span>
                    <i className={'icon iconfont icon-dot'}></i>
                  </span>
                  <span
                    title={menuData.name}>{toSideMenus.indexOf(menuData.id) >= 0 ? menuData.name : menuData.name}</span>
                </span>
            </NavLink>
          }
        </Menu.Item>)
      }
    }
    return vdom
  }

  setMenuKey (openMenuKey, props) {
    let {menuData = [], current} = props || this.props
    let pathname = current
    let selectMenuKey
    if (pathname === '/') {
      pathname = '/set/at'
    }
    for (let i = 0; i < menuData.length; i++) {
      let item = menuData[i]
      let children = item.children
      for (let j = 0; j < children.length; j++) {
        if (pathname.indexOf(children[j].path) > -1) {
          selectMenuKey = children[j].id
          break
        }
      }
      if (selectMenuKey && !openMenuKey) {
        openMenuKey = item.id
        break
      }
    }
    // 没有找到selectMenuKey，说明咩有权限访问；需要保证从二级菜单中进去的页面都包含二级菜单链接 如： /order /order/detail;后面的完全包含前面的
    if (!selectMenuKey) {
      window.location.href = menuData[0].children[0].path
    }
    this.setState({
      openMenuKey: openMenuKey + '',
      selectMenuKey: selectMenuKey + '',
      menuData
    })
  }

  render () {
    let {menuData = []} = this.props

    if (!menuData.length) {
      return <div></div>
    }
    let {openMenuKey, selectMenuKey} = this.state
    const toSideMenus = menuData.map(item => item.id)
    
    return (
      <Menu
        className='xfyun-menu scope-menu-siler'
        theme={this.state.theme}
        onClick={this.handleClick}
        style={{width: '100%'}}
        onOpenChange={this.openMenu}
        onSelect={this.selectMenu}
        openKeys={[openMenuKey]}
        selectedKeys={[selectMenuKey]}
        mode='inline'>
        {this.renderMenu(menuData, toSideMenus)}
      </Menu>
    )
  }
}

const menuMSTP = (state) => {
  return {
    menuData: state.main.menuData,
    current: state.main.currentMenu || window.location.pathname
  }
}

const menuMDTP = (dispatch) => {
  return {
    getMenu: (cb) => dispatch(mainAction.getMenu(cb)),
    setMenu: (selectedKey) => dispatch({type: mainAction.SET_CUR_MENU, currentItem: selectedKey}),
    onMenuChange: (selectedKeyPath) => dispatch({type: mainAction.SET_CUR_MENU, currentItem: selectedKeyPath})
  }
}
const sidemenu =  connect(menuMSTP, menuMDTP)(SideMenu)
export default sidemenu
