import { SessionContext } from "../Application";
import {Language} from "../const/ConstDefine"

class Menu {

    name;
    path;
    icon;
    tableRrn;
    children;

    constructor(authority) {
        this.path = authority.url;
        this.icon = authority.image;
        this.tableRrn = authority.tableRrn;
        
        let language = SessionContext.getLanguage();
        if (language == Language.Chinese) {
            this.name = authority.labelZh;
        } else if (language == Language.English) {
            this.name = authority.labelEn;
        } else {
            this.name = authority.labelRes;
        }
        //处理子菜单
        let subAuthorities = authority.subAuthorities;
        if (subAuthorities != null && subAuthorities != undefined) {
            let subMenus = [];
            subAuthorities.map((authority, index) => {
                let subMenu = new Menu(authority);
                subMenus[index] = subMenu;
            });
            this.children = subMenus;
        }
    }

    static buildMenu(authorityList) {
        if (Array.isArray(authorityList) && authorityList.length > 0) {
            let menus = [];
            authorityList.map((authority, index) => {
                let menu = new Menu(authority);
                menus[index] = menu;
            });
            return menus;
        }
        return null;
    }
}
export {Menu};
