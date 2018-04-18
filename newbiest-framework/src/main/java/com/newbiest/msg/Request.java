package com.newbiest.msg;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

/**
 * 所有请求的基类
 * Created by guoxunbo on 2017/9/29.
 */
@Data
public abstract class Request implements Serializable{

    public static final String UN_SUPPORT_ACTION_TYPE = "UnSupport ActionType : ";

    public static final String ACTION_GET_BY_RRN = "GetByRrn";
    public static final String ACTION_GET_BY_ID = "GetById";
    public static final String ACTION_CREATE = "Create";
    public static final String ACTION_UPDATE = "Update";
    public static final String ACTION_DELETE = "Delete";

    private RequestHeader header;

    public abstract RequestBody getBody();

}
