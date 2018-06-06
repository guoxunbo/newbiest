const USER_STORAGE_NAME = "User";

class User {

    username;
    password;
    department;
    orgRrn;

    constructor() {

    }

    setUsername(username) {
        this.username = username;
    }

    setPassword(password) {
        this.password = password;
    }

    setDepartment(department) {
        this.department = department;
    }

    setOrgRrn(orgRrn) {
        this.orgRrn = orgRrn;
    }

    static buildLoginUser(username, password) {
        let user = new User();
        user.setUsername(username);
        user.setPassword(password);
        return user;
    }
    
    static saveUserStorage(username, department, orgRrn) {
        let user = new User();
        user.setUsername(username);
        user.setDepartment(department);
        user.setOrgRrn(orgRrn);
        let str = JSON.stringify(user);
        sessionStorage.setItem(USER_STORAGE_NAME, str);
    }

    static clearUserStorage() {
        sessionStorage.removeItem(USER_STORAGE_NAME);
    }

    static getUserStorage() {
        var self = this;
        let object = sessionStorage.getItem(USER_STORAGE_NAME);
        if (object == undefined) {
            return undefined;
        } 
        return JSON.parse(object);
    }
}

export {User}