package com.newbiest.vanchip.dto.vlm;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * 生成外箱号。
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name ="getBoxID_20190403")
public class GetBoxID implements Serializable {

    //"http://tempuri.org/

    /**
     * 供应商编码，必填；
     */
    @XmlElement(name = "manufacturer")
    private String manufacturer;

    /**
     * 物料编码，必填；
     */
    @XmlElement(name = "compname")
    private String compname;

    /**
     * 数量，必填；
     */
    @XmlElement(name = "amount")
    private String amount;

    /**
     * 订购代码，非必填；
     */
    private String ordercode;

    /**
     * 备注，非必填；
     */
    private String remark;
    /**
     * 用户账号，必填；
     */
    @XmlElement(name = "pOper")
    private String pOper;

    /**
     * 用户密码，必填；
     */
    @XmlElement(name = "pOperPWD")
    private String pOperPWD;
}


