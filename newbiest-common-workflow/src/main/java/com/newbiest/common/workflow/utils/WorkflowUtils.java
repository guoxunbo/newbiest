package com.newbiest.common.workflow.utils;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.common.workflow.dto.ProcessDefinition;
import com.newbiest.common.workflow.model.WorkflowDefinition;
import com.newbiest.common.workflow.model.WorkflowProcess;
import com.newbiest.common.workflow.model.WorkflowRoute;
import com.newbiest.common.workflow.model.WorkflowStep;
import org.flowable.bpmn.BpmnAutoLayout;
import org.flowable.bpmn.model.*;
import org.flowable.bpmn.model.Process;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by guoxunbo on 2018/8/21.
 */
public class WorkflowUtils {

    public static final String START_EVENT_ID = "Start";
    public static final String END_EVENT_ID = "End";

    public static final String EXCLUSIVE_GATEWAY_ID = "ExclusiveGateway";


    public static final String START_EVENT_NAME = "com.wf_start_event_name";
    public static final String END_EVENT_NAME = "com.wf_end_event_name";

    public static final String EXCLUSIVE_GATEWAY_NAME = "com.wf_if_node_name";

    /**
     * 创建开始事件
     * @return
     */
    public static StartEvent generateStartEvent() {
        StartEvent startEvent = new StartEvent();
        startEvent.setId(START_EVENT_ID);
        startEvent.setName(START_EVENT_NAME);
        return startEvent;
    }

    /**
     * 创建结束事件
     * @return
     */
    public static EndEvent generateEndEvent() {
        EndEvent endEvent = new EndEvent();
        endEvent.setId(END_EVENT_ID);
        endEvent.setName(END_EVENT_NAME);
        return endEvent;
    }

    /**
     * 创建节点连线事件
     * @param sourceEventId 源节点
     * @param targetEventId 目标节点
     * @return
     */
    public static SequenceFlow generateSequenceFlow(String sourceEventId, String targetEventId) {
        SequenceFlow sequenceFlow = new SequenceFlow();
        sequenceFlow.setId(sourceEventId + StringUtils.SPLIT_CODE + targetEventId);
        sequenceFlow.setName(sourceEventId + StringUtils.SPLIT_CODE + targetEventId);
        sequenceFlow.setSourceRef(sourceEventId);
        sequenceFlow.setTargetRef(targetEventId);
        return sequenceFlow;
    }

    /**
     * 当满足condition条件的时候，就跳过此节点
     * @param sourceEventId 源节点
     * @param targetEventId 目标节点
     * @param skipCondition JUEL 表达式
     * @return
     */
    public static SequenceFlow generateSequenceFlowWithSkipCondition(String sourceEventId, String targetEventId, String skipCondition) {
        SequenceFlow sequenceFlow = generateSequenceFlow(sourceEventId, targetEventId);
        if (!StringUtils.isNullOrEmpty(skipCondition)) {
            sequenceFlow.setSkipExpression(skipCondition);
        }
        return sequenceFlow;
    }

    /**
     * 根据不同的condition选择去往不同的分支
     * @param sourceEventId 源节点
     * @param targetEventId 目标节点
     * @param conditionExpression JUEL 表达式
     * @return
     */
    public static SequenceFlow generateSequenceFlowWithCondition(String sourceEventId, String targetEventId, String conditionExpression) {
        SequenceFlow sequenceFlow = generateSequenceFlow(sourceEventId, targetEventId);
        if (!StringUtils.isNullOrEmpty(conditionExpression)) {
            sequenceFlow.setConditionExpression(conditionExpression);
        }
        return sequenceFlow;
    }


    /**
     * 创建互斥网关即IF流程 比如信息=A 走1，信息=B，走2
     * @return
     */
    public static ExclusiveGateway generateExclusiveGateway() {
        ExclusiveGateway gateway = new ExclusiveGateway();
        //TODO 当一个流程中有多个互斥网关的时候需要使用不同的ID，此处需要随机生成一下ID
        gateway.setId(EXCLUSIVE_GATEWAY_ID);
        gateway.setName(EXCLUSIVE_GATEWAY_NAME);
        
        return gateway;
    }

    public static BpmnModel generateBpmnModelByProcessDefinition(ProcessDefinition processDefinition) throws Exception{
        BpmnModel bpmnModel = new BpmnModel();


        return bpmnModel;
    }

    public static BpmnModel generateBpmnModelByWorkFlowDefinition(WorkflowDefinition defnition) throws Exception {
        BpmnModel bpmnModel = new BpmnModel();
        //TODO 根据工艺创建流程
        Process process = new Process();
        process.setId(defnition.getId());
        process.setName(defnition.getId());

        process.addFlowElement(generateStartEvent());
        process.addFlowElement(generateEndEvent());

        //TODO 如果是工艺 则进行连接subProcess 是路径的话则进行连接sequenceFlow
        //TODO 后续支持并行流程 以及判断流程
        if (defnition instanceof WorkflowProcess) {

        } else if (defnition instanceof WorkflowRoute) {
            List<WorkflowStep> stepList = ((WorkflowRoute) defnition).getSteps().stream().sorted(Comparator.comparing(WorkflowStep :: getSeqNo)).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(stepList)) {
                //TODO 这里暂时无法判断下一站流程
                String preTaskId = "";
                for (int i = 0; i< stepList.size(); i++) {
                    WorkflowStep step = stepList.get(i);

                    ReceiveTask receiveTask = new ReceiveTask();
                    receiveTask.setId(step.getId());
                    receiveTask.setName(step.getId());

                    SequenceFlow sequenceFlow;
                    if (i == 0) {  // 如果是第一个 则连线从start开始
                        sequenceFlow = generateSequenceFlow(START_EVENT_ID, receiveTask.getId());
                    } else if (i == stepList.size() - 1) { // 如果是最后一个 则连线从End结束
                        sequenceFlow = generateSequenceFlow(preTaskId, receiveTask.getId());
                        process.addFlowElement(sequenceFlow);

                        sequenceFlow = generateSequenceFlow(receiveTask.getId(), END_EVENT_ID);
                    } else {
                        sequenceFlow = generateSequenceFlow(preTaskId, receiveTask.getId());
                    }
                    preTaskId = receiveTask.getId();
                    process.addFlowElement(sequenceFlow);
                    process.addFlowElement(receiveTask);
                }
            }
        } else {
            //TODO 抛出nonsupport异常
            throw new ClientException("");
        }
        bpmnModel.addProcess(process);
        new BpmnAutoLayout(bpmnModel).execute();
        return bpmnModel;
    }

    private static void buildSequenceFlowId(String sour) {

    }

}
