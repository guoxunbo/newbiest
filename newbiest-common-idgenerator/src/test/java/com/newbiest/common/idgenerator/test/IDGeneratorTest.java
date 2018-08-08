package com.newbiest.common.idgenerator.test;

import com.google.common.collect.Maps;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.common.idgenerator.Application;
import com.newbiest.common.idgenerator.model.*;
import com.newbiest.common.idgenerator.service.GeneratorService;
import com.newbiest.common.idgenerator.utils.GeneratorContext;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

/**
 * Created by guoxunbo on 2018/8/8.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
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
    public void saveGeneratorRule() {
        GeneratorRule generatorRule = new GeneratorRule();
        generatorRule.setName("CreateTestId");
        generatorRule.setDescription("Test");
        generatorRule.setRuleType("Test");

        //FYYYYMMDDLotId0001
        List<GeneratorRuleLine> lines = Lists.newArrayList();
        FixedStringRuleLine fixedLine = new FixedStringRuleLine();
        fixedLine.setFixedString("F");
        lines.add(fixedLine);

        DateRuleLine dateRuleLine = new DateRuleLine();
        dateRuleLine.setDateFormat(DateRuleLine.DATE_FORMAT_YYYY);
        lines.add(dateRuleLine);

        dateRuleLine = new DateRuleLine();
        dateRuleLine.setDateFormat(DateRuleLine.DATE_FORMAT_MM);
        lines.add(dateRuleLine);

        dateRuleLine = new DateRuleLine();
        dateRuleLine.setDateFormat(DateRuleLine.DATE_FORMAT_DAY);
        lines.add(dateRuleLine);

        VariableRuleLine variableRuleLine = new VariableRuleLine();
        variableRuleLine.setParameter("lotId");
        variableRuleLine.setLength(4L);
        lines.add(variableRuleLine);

        SequenceRuleLine sequenceRuleLine = new SequenceRuleLine();
        sequenceRuleLine.setMin("1");
        sequenceRuleLine.setMax("9999");
        sequenceRuleLine.setLength(4L);
        lines.add(sequenceRuleLine);

        generatorRule.setRuleLines(lines);
        baseService.saveEntity(generatorRule, sessionContext);
    }

    @Test
    public void generatorId() {
        GeneratorContext context = new GeneratorContext();
        context.setRuleName("CreateTestId");
        Map<String, Object> paramterMap = Maps.newHashMap();
        paramterMap.put("lotId", "M912");
        context.setParameterMap(paramterMap);
        String id = generatorService.generatorId(sessionContext.getOrgRrn(),context);
        Assert.assertEquals("F20180808M9120002", id);
    }

    @Test
    public void generatorRadixSequence() {
        GeneratorRule generatorRule = new GeneratorRule();
        generatorRule.setName("CreateTestId2");
        generatorRule.setDescription("Test");
        generatorRule.setRuleType("Test");

        List<GeneratorRuleLine> lines = Lists.newArrayList();

        SequenceRuleLine sequenceRuleLine = new SequenceRuleLine();
        sequenceRuleLine.setMin("1");
        sequenceRuleLine.setMax("9999");
        sequenceRuleLine.setLength(2L);
        sequenceRuleLine.setSequenceType(SequenceRuleLine.SEQUENCE_TYPE_RADIX);
        lines.add(sequenceRuleLine);

        generatorRule.setRuleLines(lines);
        baseService.saveEntity(generatorRule, sessionContext);

        GeneratorContext context = new GeneratorContext();
        context.setRuleName("CreateTestId2");
        for (int i = 0; i < 20; i++) {
            String id = generatorService.generatorId(sessionContext.getOrgRrn(),context);
            // 此处Assert没写
            System.out.println(id);
        }
    }

    @Test
    public void deleteGeneratorRule() {
        generatorService.deleteGeneratorRule(5L, sessionContext);
    }

}
