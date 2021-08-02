package com.newbiest.mms.rest.doc.issue.create;

import com.newbiest.base.msg.ResponseBody;
import com.newbiest.mms.model.Document;
import lombok.Data;

@Data
public class CreateIssueOrderResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private String documentId;

	private Document document;
}
