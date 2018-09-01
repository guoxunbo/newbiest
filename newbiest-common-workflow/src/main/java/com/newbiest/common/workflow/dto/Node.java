package com.newbiest.common.workflow.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by guoxunbo on 2018/8/23.
 */
@Data
public class Node implements Serializable {

    private static final long serialVersionUID = -4831935520829529610L;

    private String id;

    private String name;

    protected String generatorId() {
        return "";
    }

    protected String gerneratorName() {
        return "";
    }

}
