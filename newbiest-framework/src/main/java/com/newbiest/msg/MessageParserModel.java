package com.newbiest.msg;

/**
 * Created by guoxunbo on 2017/9/29.
 */
public class MessageParserModel {

    private String messageName;
    private Class<? extends Request> requestClass;
    private Class<? extends Response> responseClass;

    private MessageParser parser;

    public String getMessageName() {
        return messageName;
    }

    public void setMessageName(String messageName) {
        this.messageName = messageName;
    }

    public Class<? extends Request> getRequestClass() {
        return requestClass;
    }

    public void setRequestClass(Class<? extends Request> requestClass) {
        this.requestClass = requestClass;
    }

    public Class<? extends Response> getResponseClass() {
        return responseClass;
    }

    public void setResponseClass(Class<? extends Response> responseClass) {
        this.responseClass = responseClass;
    }

    public MessageParser getParser() {
        return parser;
    }

    public void setParser(MessageParser parser) {
        this.parser = parser;
        parser.setModel(this);
    }
}
