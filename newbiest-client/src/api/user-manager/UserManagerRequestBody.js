import User from "../dto/security/User"

const ActionType = {
    Login: "Login",
    ChangePassword: "ChangePassword",
    ResetPassword: "RestPassword",
    GetAuthority: "GetAuthority"
}
export default class UserManagerRequestBody {

    actionType;
    user;

    constructor(actionType, user){
        this.actionType = actionType;
        this.user = user;
    }

    static buildLoginRequestBody(username, password) {
        let user = User.buildLoginUser(username, password);
        return new UserManagerRequestBody(ActionType.Login, user);
    }

    static buildGetAuthorityBody(username) {
        let user = new User();
        user.setUsername(username);
        return new UserManagerRequestBody(ActionType.GetAuthority, user);
    }
}
