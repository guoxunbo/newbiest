export default class User {
    objectRrn;
    username;
    description;
    sex;
    email;
    password;
    phone;
    department;
    pwdChanged;
    pwdLife;
    pwdExpiry;
    lockVersion;
    
    newPassword;
    orgRrn;

    constructor(user) {
        if (user) {
            this.objectRrn = user.objectRrn;
            this.username = user.username;
            this.description = user.description;
            this.sex = user.sex;
            this.email = user.email;
            this.password = user.password;
            this.phone = user.phone;
            this.department = user.department;
            this.pwdChanged = user.pwdChanged;
            this.pwdLife = user.pwdLife;
            this.pwdExpiry = user.pwdExpiry;
            this.lockVersion = user.lockVersion;
        }
    }

    setObjectRrn(objectRrn) {
        this.objectRrn = objectRrn;
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

    setNewPassword(newPassword) {
        this.newPassword = newPassword;
    }
    
    static buildLoginUser(username, password) {
        let user = new User();
        user.setUsername(username);
        user.setPassword(password);
        return user;
    }

}
