package com.newbiest.common.workflow.dto;

import lombok.Data;

import java.util.List;

/**
 * Created by guoxunbo on 2018/8/23.
 */
@Data
public class ProcessDefinition {

    private List<Node> nodeList;

}
