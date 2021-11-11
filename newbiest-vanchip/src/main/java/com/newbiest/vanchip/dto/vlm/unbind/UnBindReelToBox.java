package com.newbiest.vanchip.dto.vlm.unbind;

import com.newbiest.vanchip.dto.vlm.VLMModel;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 箱号与料盘解除绑定。
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name ="UnbindBox")
public class UnBindReelToBox extends VLMModel {

    /**
     * 箱号，必填；
     */
    private String boxid;

    private String pOper;

    private String pOperPWD;

}
