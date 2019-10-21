package com.newbiest.gc.rest.validationDocumentLine;

import com.newbiest.mms.model.DocumentLine;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

import java.util.List;

@Data
public class ValidationDocumentLineResponseBody extends ResponseBody {

    private static final long serialVersionUID = 1L;

    private List<DocumentLine> documentLineList;
}
