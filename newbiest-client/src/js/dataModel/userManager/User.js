class User {

    username;
    password;

    constructor() {

    }

    setUsername(username) {
        this.username = username;
    }

    setPassword(password) {
        this.password = password;
    }

    static buildLoginUser(username, password) {
        let user = new User();
        user.setUsername(username)
        user.setPassword(password)
        return user;
    }
    
}

export {User}