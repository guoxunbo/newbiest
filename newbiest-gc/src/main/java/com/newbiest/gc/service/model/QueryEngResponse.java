package com.newbiest.gc.service.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author guoxunbo
 * @date 2020-08-09 16:24
 */
@Data
public class QueryEngResponse implements Serializable {

    public static final String SUCCESS_CODE = "200";

    private String code;

    private String message;

    private List<Map> data;
}
