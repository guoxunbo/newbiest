package com.newbiest.vanchip.dto.mes;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class MesResponseHeader implements Serializable {

    public static final String RESULT_SUCCESS = "SUCCESS";
    public static final String RESULT_FAIL = "FAIL";

    private String transactionId;

    private String result;

    private String resultCode;

    private List<String> parameters;
}
