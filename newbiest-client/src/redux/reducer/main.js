import * as mainAction from '../action/main'
export const main = (state = {}, action = {}) => {
  switch (action.type) {
    case mainAction.SET_CUR_MENU: {
      // (action.currentItem || []).reverse()
      return Object.assign({}, state, {currentMenu: action.currentItem})
    }
    case mainAction.GET_MENU: {
      return Object.assign({}, state, {menuData: action.menuData})
    }
    default: {
      return state
    }
  }
}
