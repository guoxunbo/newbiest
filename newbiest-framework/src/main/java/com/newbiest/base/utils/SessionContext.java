package com.newbiest.base.utils;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;


/**
 * 客户端调用服务端时候传递信息 如用户等
 * Created by guoxunbo on 2017/10/5.
 */
@Data
public class SessionContext implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private Long orgRrn;
	private String username;

	/**
	 * 事务序列号
	 */
	private String transRrn;

	/**
	 * 方法在事务中的顺序
	 */
	private Long transSeqNo = 1L;
	
	private Date transTime;

	private String clientType;
	private String clientName;
	private String clientIp;

	public void buildTransInfo() {
		if (this.getTransRrn() == null) {
			this.setTransRrn(UUID.randomUUID().toString());
			this.setTransTime(new Date());
		} else {
			transSeqNo++;
		}
	}

	public static SessionContext buildSessionContext(long orgRrn) {
		SessionContext sessionContext = new SessionContext();
		sessionContext.setOrgRrn(orgRrn);
		return sessionContext;
	}

}
