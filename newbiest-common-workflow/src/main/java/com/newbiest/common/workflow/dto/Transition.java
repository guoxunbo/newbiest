package com.newbiest.common.workflow.dto;

import com.newbiest.base.utils.StringUtils;
import lombok.Data;

import java.util.Random;

/**
 * Created by guoxunbo on 2018/8/23.
 */
@Data
public class Transition extends Node {

    /**
     * 源节点
     */
    private String sourceNodeId;

    /**
     * 目标节点
     */
    private String targetNodeId;

    /**
     *
     */
    private String skipCondition;

    /**
     *
     */
    private String expressCondition;

    /**
     * 生成ID为sourceNodeId-targetNodeId-随机一个100以内的数字
     * 为了防止一个流程中有出现2次以上包括2次的前后节点一致，故在此处加入随机数字
     * @return
     */
    @Override
    protected String generatorId() {
        Random random = new Random();
        int randomNumber = random.ints(0, 100).limit(1).boxed().findFirst().get();
        return sourceNodeId + StringUtils.SPLIT_CODE + targetNodeId + StringUtils.SPLIT_CODE + randomNumber;
    }

    @Override
    protected String gerneratorName() {
        return sourceNodeId + StringUtils.SPLIT_CODE + targetNodeId;
    }
}
