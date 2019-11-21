package com.newbiest.ams.action;

import com.newbiest.ams.model.AlarmData;
import com.newbiest.ams.model.AlarmJobLayer;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.service.BaseService;
import com.newbiest.security.service.SecurityService;
import lombok.Data;

import java.io.Serializable;

/**
 * 警报动作。触发了警报之后的动作。比如邮件，weChat或者call等
 * Created by guoxunbo on 2019-11-19 13:51
 */
@Data
public abstract class AlarmAction implements Serializable {

    public static final String ACTION_TYPE_EMAIL = "Email";
    public static final String ACTION_TYPE_WE_CHAT = "WeChat";

    protected AlarmData alarmData;

    protected String toUser;

    protected String toRole;

    protected String templateId;

    protected SecurityService securityService;

    abstract void triggerAction() throws ClientException;

    /**
     * AlarmAction
     * @param alarmActionContext
     * @return
     */
    public static AlarmAction createAction(AlarmActionContext alarmActionContext) {
        AlarmAction alarmAction = null;
        AlarmJobLayer alarmJobLayer = alarmActionContext.getAlarmJobLayer();
        AlarmData alarmData = alarmActionContext.getAlarmData();

        String actionType = alarmJobLayer.getActionType();
        if (ACTION_TYPE_EMAIL.equals(actionType)) {
            alarmAction = new AlarmEmailAction();
        } else if (ACTION_TYPE_WE_CHAT.equals(actionType)) {

        }

        if (alarmAction != null) {
            alarmAction.setAlarmData(alarmData);
            alarmAction.setToUser(alarmJobLayer.getToUser());
            alarmAction.setToRole(alarmJobLayer.getToRole());
            alarmAction.setSecurityService(alarmActionContext.getSecurityService());
        }
        return alarmAction;
    }
}
