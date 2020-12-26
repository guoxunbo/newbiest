package com.newbiest.mms.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 物料批次判定动作
 * @author guoxunbo
 * @date 12/25/20 5:22 PM
 */
@Data
public class MaterialLotJudgeAction implements Serializable {

    private String materialLotId;

    private String judgeResult;

    private String actionCode;

    private String actionReason;

    private String actionComments;

}
