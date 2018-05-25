class UserManagerRequestBody {

    actionType;
    user;

    constructor(actionType, user){
        this.actionType = actionType;
        this.user = user;
    }

}

export {UserManagerRequestBody}