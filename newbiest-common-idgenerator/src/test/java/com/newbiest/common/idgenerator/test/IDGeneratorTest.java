package com.newbiest.common.idgenerator.test;

import com.google.common.collect.Maps;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.common.idgenerator.model.*;
import com.newbiest.common.idgenerator.service.GeneratorService;
import com.newbiest.common.idgenerator.utils.GeneratorContext;
import com.newbiest.main.FrameworkApplication;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

/**
 * Created by guoxunbo on 2018/8/8.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = FrameworkApplication.class)
@ActiveProfiles("dev")
public class IDGeneratorTest {

    protected SessionContext sessionContext;

    @Autowired
    BaseService baseService;

    @Autowired
    GeneratorService generatorService;

    @Before
    public void init() {
        sessionContext = new SessionContext();
        sessionContext.setOrgRrn(1L);
    }

    @Test
    public void generatorId() {

        GeneratorContext generatorContext = new GeneratorContext();
        generatorContext.setRuleName("CreateMLot");
        String id = generatorService.generatorId(sessionContext.getOrgRrn(), generatorContext);
        System.out.println(id);
    }
}
