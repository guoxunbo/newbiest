package com.newbiest.msg;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.newbiest.base.utils.DateUtils;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Created by guoxunbo on 2017/10/8.
 */
public class DefaultParser {

    public static String writerJson(Object object) throws Exception {
        ObjectWriter jsonWriter = getObjectMapper().writerWithDefaultPrettyPrinter();
        return jsonWriter.forType(object.getClass()).writeValueAsString(object);
    }

    public static ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.enable(MapperFeature.USE_WRAPPER_NAME_AS_PROPERTY_NAME);
        objectMapper.setTimeZone(TimeZone.getDefault());
        return objectMapper;
    }
}
