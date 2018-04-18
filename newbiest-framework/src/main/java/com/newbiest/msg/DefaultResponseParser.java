package com.newbiest.msg;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collections;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;


public class DefaultResponseParser extends DefaultParser{

	private static final Logger logger = Logger.getLogger(DefaultResponseParser.class);

	public static Response readerJson(String jsonString) throws Exception {
		ObjectReader jsonReader = getObjectMapper().readerFor(DefaultResponse.class);
		return jsonReader.readValue(jsonString);
	}

}
