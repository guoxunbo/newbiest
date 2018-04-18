package com.newbiest.msg;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collections;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.fasterxml.jackson.databind.*;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

/**
 * Created by guoxunbo on 2017/9/29.
 */
public class DefaultRequestParser extends DefaultParser{

	private static final Logger logger = Logger.getLogger(DefaultRequestParser.class);

	public static Request readerJson(String jsonString) throws Exception {
		ObjectReader jsonReader = getObjectMapper().readerFor(DefaultRequest.class);
		return jsonReader.readValue(jsonString);
	}

}
