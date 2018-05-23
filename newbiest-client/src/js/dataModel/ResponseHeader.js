class ResponseHeader{

    transactionId;
	result;
	resultCode;
	resultChinese;
	resultEnglish;
    resultRes;
    meesageRrn;

    constructor(header) {
        this.transactionId = header.transactionId;
        this.result = header.result;
        this.resultCode = header.resultCode;
        this.resultChinese = header.resultChinese;
        this.resultEnglish = header.resultEnglish;
        this.resultRes = header.resultRes;
        this.messageRrn = header.messageRrn;
    }
    
}
export {ResponseHeader}
