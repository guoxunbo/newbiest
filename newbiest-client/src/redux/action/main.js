import { SessionContext } from '../../api/Application';
import { Notification } from '../../components/notice/Notice';
import Request from '../../api/Request';
import UserManagerRequestHeader from "../../api/user-manager/UserManagerRequestHeader";
import UserManagerRequestBody from "../../api/user-manager/UserManagerRequestBody";
import MessageUtils from '../../api/utils/MessageUtils';
import { UrlConstant } from '../../api/const/ConstDefine';

export const GET_MENU = 'get_menu'
export const SET_CUR_MENU = 'set_cur_menu'
export const SET_MODULE_ID = 'set_module_id'
export const SET_BREADCRUMB = 'set_breakcrumb'

/**
 * 获取左边菜单信息
 */

export const getMenu = (cb) => {
  return function (dispatch) {
    let sessionContext = SessionContext.getSessionContext();
    if (sessionContext == undefined) {
      Notification.showInfo("请先登陆");
      this.props.history.push('/');
      return;
    }
  
    let requestBody = UserManagerRequestBody.buildGetAuthorityBody(SessionContext.getUsername());
    let requestHeader = new UserManagerRequestHeader();
    let request = new Request(requestHeader, requestBody, UrlConstant.UserManagerUrl);
    var self = this;
    let requestObject = {
      request: request,
      success: function(responseBody) {
        let menuConfig = AsideMenu.buildMenu(responseBody.user.authorities);
        let newData = []
      // 去掉没有二级菜单的一级菜单
      for(let i=0; i<menuConfig.length; i++){
        if(menuConfig[i].children && menuConfig[i].children.length){
          newData.push(menuConfig[i])
        }
      }
      // 如果设置的默认首页/set/at没有权限访问，则使用数组中第一个路径
      if(newData.length){
        let path = newData[0].children[0].path
        if(window.location.pathname === '/' && path !== '/set/at'){
          window.location.href = path
        }
      }
        dispatch({type: GET_MENU, menuData: menuConfig})
        cb && cb()
       // self.setState({asideMenuConfig: menuConfig});
      }
    }
    MessageUtils.sendRequest(requestObject);
  }
}
