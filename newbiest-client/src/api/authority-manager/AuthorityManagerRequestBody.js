
const ActionType = {
    GetAuthorityTree: "GetAuthorityTree",
}
export default class AuthorityManagerRequestBody {
    actionType;
    authortity;

    constructor(actionType) {
        this.actionType = actionType;
    }
    
    static buildGetAuthorityTreeBody = () => {
        return new AuthorityManagerRequestBody(ActionType.GetAuthorityTree);
    }
    
}