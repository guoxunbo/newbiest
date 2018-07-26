package com.newbiest.security;

import com.newbiest.base.factory.ModelFactory;
import com.newbiest.security.model.NBRole;
import com.newbiest.security.model.NBRoleHis;
import com.newbiest.security.model.NBUser;
import com.newbiest.security.model.NBUserHis;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by guoxunbo on 2017/10/14.
 */
@Component
public class HistoryManagerBean {

    @PostConstruct
    public void init() {
        ModelFactory.registerHistoryModelClassLoader(NBUser.class.getName(), NBUserHis.class.getClassLoader());
        ModelFactory.registerHistoryClassName(NBUser.class.getName(), NBUserHis.class.getName());

        ModelFactory.registerHistoryModelClassLoader(NBRole.class.getName(), NBUserHis.class.getClassLoader());
        ModelFactory.registerHistoryClassName(NBRole.class.getName(), NBRoleHis.class.getName());
    }
}
