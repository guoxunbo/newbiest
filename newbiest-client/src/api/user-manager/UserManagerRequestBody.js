import User from "../dto/security/User"

const ActionType = {
    Login: "Login",
    ChangePassword: "ChangePassword",
    ResetPassword: "RestPassword",
    GetAuthority: "GetAuthority",
    Create: "Create",
    Update: "Update"
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

    static buildChangePwdBody(username, password, newPassword) {
        let user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setNewPassword(newPassword);
        return new UserManagerRequestBody(ActionType.ChangePassword, user);
    }

    static buildResetPwdBody(username) {
        let user = new User();
        user.setUsername(username);
        return new UserManagerRequestBody(ActionType.ResetPassword, user);
    }

    static buildMergeUserBody(values) {
        let user = new User(values);
        let actionType = ActionType.Create;
        if (user.objectRrn) {
            actionType = ActionType.Update;
        }
        return new UserManagerRequestBody(actionType, user);

    }

}
