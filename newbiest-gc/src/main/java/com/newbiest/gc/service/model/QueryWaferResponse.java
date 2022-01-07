package com.newbiest.gc.service.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author guozhangLuo
 * @date 2021-11-04 18：26
 */
@Data
public class QueryWaferResponse implements Serializable {

    public static final String SUCCESS_CODE = "200";

    private String code;

    private String message;

    private List<Map<String, String>> data;
}
