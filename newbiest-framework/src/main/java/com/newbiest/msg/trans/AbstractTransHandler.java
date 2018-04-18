package com.newbiest.msg.trans;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.msg.MessageParser;
import com.newbiest.msg.MessageParserFactory;
import com.newbiest.msg.Request;
import com.newbiest.msg.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTransHandler implements ITransHandler {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public AbstractTransHandler() {
		initMessageParser();
	}
	
	public String execute(TransContext context) throws Exception{
		TransContext ctx = internalExecute(context);
		return ctx.getResponse();
	}
	
	public abstract void initMessageParser();
	
	public abstract MessageParser getMessageParser();
	
	protected abstract TransContext internalExecute(TransContext context) throws Exception;
	
	public Object executeResponse(Response response, TransContext context) throws Exception {
		return null;
	}
	
	public MessageParser getMessageParser(String type) {
		try {
			MessageParser parser = MessageParserFactory.getMessageParser(type);
			if (parser == null) {
				initMessageParser();
				return MessageParserFactory.getMessageParser(type);
			}
			return parser;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}
	
	public SessionContext getSessionContext(Request request, TransContext context) throws ClientException {
		SessionContext sc = new SessionContext();
		if (request.getHeader().getOrgRrn() != null) {
			sc.setOrgRrn(request.getHeader().getOrgRrn());
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Request TransactionId is [" + request.getHeader().getTransactionId() + "] orgRrn is null");
			}
			sc.setOrgRrn(0L);
		}
		sc.setUsername(request.getHeader().getUsername());
		return sc;
	}

}
