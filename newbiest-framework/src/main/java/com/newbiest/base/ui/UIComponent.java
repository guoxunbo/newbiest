package com.newbiest.base.ui;

import com.newbiest.base.factory.ModelFactory;
import com.newbiest.base.ui.model.*;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by guoxunbo on 2018/8/1.
 */
@Component
public class UIComponent {

    @PostConstruct
    public void init() {
        //注册modelClassLoader
        ModelFactory.registerModelClassLoader(NBOwnerReferenceName.class.getName(), NBOwnerReferenceName.class.getClassLoader());
        ModelFactory.registerModelClassLoader(NBOwnerReferenceList.class.getName(), NBOwnerReferenceList.class.getClassLoader());
        ModelFactory.registerModelClassLoader(NBSystemReferenceName.class.getName(), NBSystemReferenceName.class.getClassLoader());
        ModelFactory.registerModelClassLoader(NBSystemReferenceList.class.getName(), NBSystemReferenceList.class.getClassLoader());
        ModelFactory.registerModelClassLoader(NBTable.class.getName(), NBTable.class.getClassLoader());
        ModelFactory.registerModelClassLoader(NBTab.class.getName(), NBTab.class.getClassLoader());
        ModelFactory.registerModelClassLoader(NBTable.class.getName(), NBTable.class.getClassLoader());
        ModelFactory.registerModelClassLoader(NBField.class.getName(), NBField.class.getClassLoader());

    }
}
