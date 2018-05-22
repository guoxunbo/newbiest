class RequestHeader{  

    constructor(messageName, transactionId, orgRrn, orgName, userName){  
        this.messageName = messageName;
        this.transactionId = transactionId;
        this.orgRrn = orgRrn;
        this.orgName = orgName;
        this.userName = userName;
    }
}  
export {RequestHeader};  