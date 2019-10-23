package com.newbiest.gc.rest.validationDocumentLine;

import com.newbiest.msg.Response;
import lombok.Data;

@Data
public class ValidationDocumentLineResponse extends Response {

    private static final long serialVersionUID = 1L;

    private ValidationDocumentLineResponseBody body;
}
