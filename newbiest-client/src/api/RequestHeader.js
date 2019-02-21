import { SessionContext } from "./Application";
import uuid from 'react-native-uuid';

export default class RequestHeader{  

    messageName;
    transactionId;
    orgName;
    userName;
    orgRrn;
    token;

    constructor(messageName){  
        let sessionContext = SessionContext.getSessionContext();
        this.messageName = messageName;
        this.transactionId = uuid.v4();
        if (sessionContext != undefined) {
            this.orgRrn = sessionContext.orgRrn;
            this.userName = sessionContext.username;
            this.token = SessionContext.getToken();
        } 
    }

}  