import {Language} from "../../const/ConstDefine"

export default class Authority {

    name;
    path;
    icon;
    tableRrn;
    children;
    
    constructor(authority, language) {
        this.icon = authority.image;
        this.tableRrn = authority.tableRrn;
        this.path = authority.url + "/" + this.tableRrn;
        if (language == Language.Chinese) {
            this.name = authority.labelZh;
        } else if (language == Language.English) {
            this.name = authority.labelEn;
        } else {
            this.name = authority.labelRes;
        }
        //处理子菜单
        let subAuthorities = authority.subAuthorities;
        if (subAuthorities) {
            let subMenus = [];
            subAuthorities.map((authority, index) => {
                let subMenu = new Authority(authority, language);
                subMenus[index] = subMenu;
            });
            this.children = subMenus;
        }
    }

    static buildMenu(authorityList, language) {
        if (Array.isArray(authorityList) && authorityList.length > 0) {
            let menus = [];
            authorityList.map((authority, index) => {
                let menu = new Authority(authority, language);
                menus[index] = menu;
            });
            return menus;
        }
        return null;
    }
}
