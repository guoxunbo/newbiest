package com.newbiest.rms.main;

import com.newbiest.base.factory.ModelFactory;
import com.newbiest.rms.model.Equipment;
import com.newbiest.rms.model.Recipe;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by guoxunbo on 2019/1/14.
 */
@Component
public class RmsComponent {

    @PostConstruct
    public void init() {
        //注册modelClassLoader
        ModelFactory.registerModelClassLoader(Equipment.class.getName(), Equipment.class.getClassLoader());
        ModelFactory.registerModelClassLoader(Recipe.class.getName(), Recipe.class.getClassLoader());
    }

}
