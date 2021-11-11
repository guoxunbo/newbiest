package com.newbiest.vanchip.dto.vlm.box;

import com.newbiest.vanchip.dto.vlm.VLMModel;
import lombok.Data;

import javax.xml.bind.annotation.*;

/**
 * 生成外箱号。
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name ="getBoxID_20190403")
public class GetBoxID extends VLMModel {

    /**
     * 供应商编码，必填；
     */
    @XmlElement(name = "manufacturer")
    private String manufacturer;

    /**
     * 客户物料编码，必填；
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


