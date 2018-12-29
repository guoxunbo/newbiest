import Role from "../dto/security/Role";
import User from "../dto/security/User";
import Authoritity from "../dto/security/Authority";

const ActionType = {
    GetByRrn: "GetByRrn",
    DispatchUser: "DispatchUser",
    DispatchAuthority: "DispatchAuthority"
}
export default class RoleManagerRequestBody {

    constructor(actionType, role){
        this.actionType = actionType;
        this.role = role;
    }

    static buildGetByRrnBody = (roleRrn) => {
        let role = new Role();
        role.setObjectRrn(roleRrn);
        return new RoleManagerRequestBody(ActionType.GetByRrn, role);
    }
    
    static buildDispatchUserBody = (value) => {
        let role = new Role();
        role.setObjectRrn(value.roleRrn);
        
        let userKeys = value.userKeys;
        let dispatchUsers = [];
        if (userKeys) {
            userKeys.forEach((key) => {
                let dispatchUser = new User();
                dispatchUser.setObjectRrn(key);
                dispatchUsers.push(dispatchUser);
            });
        }
        role.setUsers(dispatchUsers);
        return new RoleManagerRequestBody(ActionType.DispatchUser, role);
    }

    static buildDispatchAuthorityBody = (value) => {
        let role = new Role();
        role.setObjectRrn(value.roleRrn);
        
        let authorityKeys = value.authorityKeys;
        let dispatchAuthories = [];
        if (authorityKeys) {
            authorityKeys.forEach((key) => {
                let authority = new Authoritity();
                authority.setObjectRrn(key);
                dispatchAuthories.push(authority);
            });
        }
        role.setAuthorities(dispatchAuthories);
        return new RoleManagerRequestBody(ActionType.DispatchAuthority, role);
    }

}