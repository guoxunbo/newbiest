package com.newbiest.msg.trans;

import com.newbiest.base.factory.ModelFactory;
import com.newbiest.base.factory.TransHandlerFactory;
import com.newbiest.msg.base.entity.EntityManagerHandler;
import com.newbiest.msg.base.entity.EntityManagerRequest;
import com.newbiest.msg.base.entitylist.EntityListHandler;
import com.newbiest.msg.base.entitylist.EntityListRequest;
import com.newbiest.msg.security.role.RoleHandler;
import com.newbiest.msg.security.role.RoleRequest;
import com.newbiest.security.model.NBRole;
import com.newbiest.security.model.NBUser;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by guoxunbo on 2017/9/29.
 */
@Component
public class TransHandlerManagerBean {

    @PostConstruct
    public void init() {
        TransHandlerFactory.registerTransHandler(EntityListRequest.MESSAGE_NAME, new EntityListHandler());
        TransHandlerFactory.registerTransHandler(EntityManagerRequest.MESSAGE_NAME, new EntityManagerHandler());

        TransHandlerFactory.registerTransHandler(RoleRequest.MESSAGE_NAME, new RoleHandler());

        ModelFactory.registerModelClassLoader(NBUser.class.getName(), NBUser.class.getClassLoader());
        ModelFactory.registerModelClassLoader(NBRole.class.getName(), NBRole.class.getClassLoader());

    }

}
