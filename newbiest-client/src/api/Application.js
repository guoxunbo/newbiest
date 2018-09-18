const Application = {
    name: '智行管理系统',
    version: "0.0.1",
    copyright: {
        name: "© 2018 By Newbiest",
        url: "https://www.baidu.com"
    },

    // 默认的分页配置
    pagination: {
        // 默认页数
        pageSize: 10,
        // 是否允许快速跳转到第几页
        showQuickJumper: true,
        // 是否可以改变 pageSize
        showSizeChanger: true,
        // 当只有1页的时候是否隐藏分页器
        hideOnSinglePage: true
    },

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
    orgRrn;

    setLanguage(language) {
        this.language = language;
    }

    setUsername(username) {
        this.username = username;
    }

    setOrgName(orgName) {
        this.orgName = orgName;
    }

    setOrgRrn(orgRrn) {
        this.orgRrn = orgRrn;
    }

    static saveSessionContext(username, orgRrn, language) {
        let sc = new SessionContext();
        sc.setLanguage(language);
        sc.setUsername(username);
        sc.setOrgRrn(orgRrn);
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
