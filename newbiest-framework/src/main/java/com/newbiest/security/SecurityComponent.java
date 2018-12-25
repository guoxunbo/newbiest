package com.newbiest.security;

import com.newbiest.base.factory.ModelFactory;
import com.newbiest.security.model.NBRole;
import com.newbiest.security.model.NBRoleHis;
import com.newbiest.security.model.NBUser;
import com.newbiest.security.model.NBUserHis;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by guoxunbo on 2018/12/25.
 */
@Component
public class SecurityComponent {

    @PostConstruct
    public void init() {
        // 注册历史相关
        ModelFactory.registerHistoryModelClassLoader(NBUser.class.getName(), NBUserHis.class.getClassLoader());
        ModelFactory.registerHistoryClassName(NBUser.class.getName(), NBUserHis.class.getName());

        ModelFactory.registerHistoryModelClassLoader(NBRole.class.getName(), NBUserHis.class.getClassLoader());
        ModelFactory.registerHistoryClassName(NBRole.class.getName(), NBRoleHis.class.getName());

        // 注册modelClass
        ModelFactory.registerModelClassLoader(NBUser.class.getName(), NBUser.class.getClassLoader());
    }
}
