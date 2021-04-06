package com.newbiest.mms.print;

import com.google.common.collect.Maps;
import com.newbiest.mms.model.LabelTemplate;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author guoxunbo
 * @date 4/6/21 3:12 PM
 */
@Data
public class PrintContext implements Serializable {

    private Object baseObject;

    private Map<String, Object> parameterMap = Maps.newHashMap();

    private LabelTemplate labelTemplate;

}
