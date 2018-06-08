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

}

export {User}