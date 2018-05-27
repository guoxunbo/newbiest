import {User} from "../userManager/User"

const ActionType = {
    login: "Login",
    changePassword: "ChangePassword",
    resetPassword: "RestPassword",
    getAuthority: "GetAuthority"
}
class UserManagerRequestBody {

    actionType;
    user;

    constructor(actionType, user){
        this.actionType = actionType;
        this.user = user;
    }

    static buildLoginRequestBody(username, password) {
        let user = User.buildLoginUser(username, password);
        return new UserManagerRequestBody(ActionType.login, user);
    }
}

export {UserManagerRequestBody}