package com.newbiest.vanchip.dto.vlm.bind;

import com.newbiest.vanchip.dto.vlm.VLMModel;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 箱号和REEL绑定
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name ="BindReelToBox")
public class BindReelToBox extends VLMModel {

    /**
     * 箱号，必填
     */
    private String boxid;

    /**
     * 料盘号，可以有多个，中间以英文字母的“,”隔开；
     */
    private String reels;

    /**
     * 用户账号，必填；
     */
    private String pOper;

    /**
     * 用户密码，必填；
     */
    private String pOperPWD;
}
