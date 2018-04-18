package com.newbiest.msg;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.newbiest.base.exception.ClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;

/**
 * 消息实例化对象
 * Created by guoxunbo on 2017/9/29.
 */
public class MessageParser {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private MessageParserModel model;

    protected ObjectReader jsonReader;
    protected ObjectWriter jsonWriter;

    public Request readRequest(String request) throws Exception{
        try {
            if (jsonReader == null) {
                ObjectMapper objectMapper = DefaultParser.getObjectMapper();
                jsonReader = objectMapper.readerFor(model.getRequestClass());
            }
            return jsonReader.readValue(request);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    public String writeResponse(Response response) throws Exception{
        try {
            if (jsonWriter == null) {
                ObjectMapper objectMapper = DefaultParser.getObjectMapper();
                jsonWriter = objectMapper.writerWithView(model.getResponseClass());
            }
            return jsonWriter.writeValueAsString(response);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    public MessageParserModel getModel() {
        return model;
    }

    public void setModel(MessageParserModel model) {
        this.model = model;
    }
}
