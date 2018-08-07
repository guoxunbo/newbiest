package com.newbiest.common.idgenerator.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.ui.service.UIService;
import com.newbiest.common.idgenerator.service.GeneratorService;
import com.newbiest.main.ApplicationContextProvider;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * IDGenerator上下文
 * Created by guoxunbo on 2018/8/3.
 */
@Data
public class GeneratorContext {

    private UIService uiService;

    private BaseService baseService;

    private GeneratorService generatorService;

    private String ruleName;

    private Map<String, Object> parameterMap = Maps.newHashMap();

    private Object object;

    private String objectType;

    private boolean newTransFlag;

    private int count = 1;

    private Integer currentIndex = null;


    public void addParameter(String name, Object value) {
        parameterMap.put(name, value);
    }

    public Object getParameter(String name) {
        return parameterMap.get(name);
    }

    public void addIdSegments(String segment){
        idSegments.add(segment);
    }

    /**
     * 存放每个RuleLine生成的值
     */
    private List<String> idSegments = Lists.newLinkedList();

    public String getIdPrefix() {
        String idPrefix = "";
        for (String idSegment : idSegments) {
            idPrefix += idSegment;
        }
        return idPrefix;
    }

}
