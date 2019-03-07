package com.newbiest.base.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 所有需要记录动作原因的父类
 * Created by guoxunbo on 2019/2/28.
 */
@Data
public class Action implements Serializable{

    public String actionCode;

    public String actionReason;

    public String actionComment;

}
