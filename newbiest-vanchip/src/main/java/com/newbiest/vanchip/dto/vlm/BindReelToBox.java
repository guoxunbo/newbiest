package com.newbiest.vanchip.dto.vlm;

import lombok.Data;

import java.io.Serializable;

/**
 * 箱号和REEL绑定
 */
@Data

public class BindReelToBox implements Serializable {

    /**
     * 箱号，必填
     */
    private String boxid;

    /**
     * 料盘号，可以有多个，中间以英文字母的“,”隔开；
     */
    private String reels;

    /**
     * :用户账号，必填；
     */
    private String pOper;

    /**
     * 用户密码，必填；
     */
    private String pOperPWD;
}
