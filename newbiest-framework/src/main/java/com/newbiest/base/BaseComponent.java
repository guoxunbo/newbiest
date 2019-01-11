package com.newbiest.base;

import com.newbiest.base.factory.ModelFactory;
import com.newbiest.base.model.NBMessage;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by guoxunbo on 2019/1/11.
 */
@Component
public class BaseComponent {

    @PostConstruct
    public void init() {
        ModelFactory.registerModelClassLoader(NBMessage.class.getName(), NBMessage.class.getClassLoader());

    }
}
