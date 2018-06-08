import {Language, Org} from './const/ConstDefine';

const Application = {
    name: '智行管理系统',
    version: "0.0.1",
    copyright: {
        name: "© 2018 By Newbiest",
        url: "https://www.baidu.com"
    },
    language: [
        {label: Language.Chinese, value: Language.Chinese},
        {label: Language.English, value: Language.English},
    ],
    orgs: [
        {label: Org.zhixing, value: Org.zhixing}
    ],
    notice: {
        delay: 5000,
        // 构建button组件是否支持关闭 支持点击锁定
        button: {
            closer: true,
            sticker: true,
            labels: {
                close: 'Close', stick: 'Stick', unstick: 'Unstick'
            }
        },
        mobile: {
            swipeDismiss: true,
            styling: true
        }
    }
};

const SC_STORAGE_NAME = "SessionContext";

class SessionContext {
    language;
    username;
    orgName;

    setLanguage(language) {
        this.language = language;
    }

    setUsername(username) {
        this.username = username;
    }

    setOrgName(orgName) {
        this.orgName = orgName;
    }

    static saveSessionContext(username, orgName, language) {
        let sc = new SessionContext();
        sc.setLanguage(language);
        sc.setUsername(username);
        sc.setOrgName(orgName);
        sessionStorage.setItem(SC_STORAGE_NAME, JSON.stringify(sc));
    }

    static clearSessionContext() {
        sessionStorage.removeItem(SC_STORAGE_NAME);
    }

    static getSessionContext() {
        let object = sessionStorage.getItem(SC_STORAGE_NAME);
        if (object == undefined) {
            return undefined;
        } 
        return JSON.parse(object);
    }

    static getLanguage() {
        let sessionContext = this.getSessionContext();
        if (sessionContext == undefined) {
            return undefined;
        }
        return sessionContext.language;
    }

    static getUsername() {
        let sessionContext = this.getSessionContext();
        if (sessionContext == undefined) {
            return undefined;
        }
        return sessionContext.username;
    }
}

export {Application, SessionContext}
