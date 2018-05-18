package com.newbiest.msg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.NONE)
public class RequestHeader implements Serializable {

	private static final long serialVersionUID = 1L;

	@XmlElement(name="MessageName")
	private String messageName;
	
	@XmlElement(name="TransactionId")
	private String transactionId;
	
	@XmlElement(name="OrgRrn")
	private Long orgRrn;
	
	@XmlElement(name="OrgName")
	private String orgName;
	
	@XmlElement(name="Username")
	private String username;
	
	public String getMessageName() {
		return messageName;
	}

	public void setMessageName(String messageName) {
		this.messageName = messageName;
	}
	
	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public Long getOrgRrn() {
		return orgRrn;
	}

	public void setOrgRrn(Long orgRrn) {
		this.orgRrn = orgRrn;
	}
	
	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String userName) {
		this.username = userName;
	}
}
