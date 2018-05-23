class RequestHeader{  

    messageName;
    transactionId;
    orgRrn;
    orgName;
    userName;
    
    constructor(messageName, orgRrn, orgName, userName){  
        this.messageName = messageName;
        this.transactionId = this.generatorUUID();
        this.orgRrn = orgRrn;
        this.orgName = orgName;
        this.userName = userName;
    }

    generatorUUID() {
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
            var r = Math.random() * 16 | 0,
            v = c == 'x' ? r : (r & 0x3 | 0x8);
            return v.toString(16);
        }).toUpperCase();
    }
}  
export {RequestHeader};  