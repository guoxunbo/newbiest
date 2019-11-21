package com.newbiest.ams.action;

import com.newbiest.ams.model.AlarmData;
import com.newbiest.ams.model.AlarmJobLayer;
import com.newbiest.security.service.SecurityService;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by guoxunbo on 2019-11-19 16:37
 */
@Data
@AllArgsConstructor
public class AlarmActionContext implements Serializable {

    private AlarmJobLayer alarmJobLayer;

    private AlarmData alarmData;

    private SecurityService securityService;

}
