package com.newbiest.vanchip.dto.vlm;

import lombok.Data;

import java.io.Serializable;

/**
 * 箱号与料盘解除绑定。
 */
@Data
public class UnBindReelToBox implements Serializable {

    /**
     * 箱号，必填；
     */
    private String boxID;

    private String pOper;

    private String pOperPWD;

}
