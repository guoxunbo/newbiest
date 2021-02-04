package com.newbiest.ams.dto;

import lombok.Data;

import java.util.Date;

/**
 * 触发alarm的Dto对象
 *  供第三方系统调用。UI直接操作AlarmData
 * Created by guoxunbo on 2019-11-19 16:51
 */
@Data
public class AlarmMessage  {

    private String objectId;

    private String objectType;

    private String objectOwner;

    private String name;

    private String text;

    private String category;

    private String type;

    private Date triggerTime;

    private String comments;

    private String severityLevel;

    private Long priority;


}
