package com.newbiest.msg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by guoxunbo on 2017/9/29.
 */
public class MessageParserFactory {
	
	public static Map<String, MessageParserModel> parserMap = new ConcurrentHashMap<String, MessageParserModel>(); 
	
	public static void registerMessageParser(String type, MessageParserModel model) {
		parserMap.put(type, model);
	}
	
	public static MessageParser getMessageParser(String type) throws Exception{
		if (parserMap.containsKey(type)) {
			MessageParserModel model = parserMap.get(type);
			if (model.getParser() != null) {
				return model.getParser();
			}
			
			List<Class> classList = new ArrayList<Class>();
			if (model.getRequestClass() != null) {
				classList.add(model.getRequestClass());
			}
			if (model.getResponseClass() != null) {
				classList.add(model.getResponseClass());
			}
			MessageParser parser = new MessageParser();
			model.setParser(parser);
			
			return parser;
		} 
		return null;
	}
}
