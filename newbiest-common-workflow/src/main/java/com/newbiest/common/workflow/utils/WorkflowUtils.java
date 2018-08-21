package com.newbiest.common.workflow.utils;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.common.workflow.model.WorkflowDefinition;
import com.newbiest.common.workflow.model.WorkflowProcess;
import com.newbiest.common.workflow.model.WorkflowRoute;
import org.flowable.bpmn.model.*;
import org.flowable.bpmn.model.Process;
import org.flowable.validation.ProcessValidator;
import org.flowable.validation.ProcessValidatorFactory;
import org.flowable.validation.ValidationError;

import java.util.List;

/**
 * Created by guoxunbo on 2018/8/21.
 */
public class WorkflowUtils {

    private static final String START_EVENT_ID = "Start";
    private static final String END_EVENT_ID = "Start";

    private static final String START_EVENT_NAME = "com.wf_start_event_name";
    private static final String START_END_NAME = "com.wf_end_event_name";

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
    public EndEvent generateEndEvent() {
        EndEvent endEvent = new EndEvent();
        endEvent.setId(END_EVENT_ID);
        endEvent.setName(START_END_NAME);
        return endEvent;
    }

    /**
     * 创建节点连线事件
     * @param id ID
     * @param name 名称
     * @param sourceEventId 源节点
     * @param targetEventId 目标节点
     * @return
     */
    public SequenceFlow generateSequenceFlow(String id, String name, String sourceEventId, String targetEventId) {
        SequenceFlow sequenceFlow = new SequenceFlow();
        sequenceFlow.setId(id);
        sequenceFlow.setName(name);
        sequenceFlow.setTargetRef(sourceEventId);
        sequenceFlow.setSourceRef(targetEventId);
        return sequenceFlow;
    }

    public BpmnModel generateBpmnModelByWorkFlowDefinition(WorkflowDefinition workflowProcess) throws Exception {
        BpmnModel bpmnModel = new BpmnModel();
        //TODO 根据工艺创建流程
        Process process = new Process();
        process.setId(workflowProcess.getObjectRrn().toString());
        process.setName(workflowProcess.getName());

        process.addFlowElement(generateStartEvent());
        process.addFlowElement(generateEndEvent());

        //TODO 如果是工艺 则进行连接subProcess 是路径的话则进行连接sequenceFlow
        //TODO 后续支持并行流程 以及判断流程
        if (workflowProcess instanceof WorkflowProcess) {

        } else if (workflowProcess instanceof WorkflowRoute) {

        } else {
            //TODO 抛出unsupport异常
            throw new ClientException("");
        }

        bpmnModel.addProcess(process);

        validateBpmnModel(bpmnModel);
        return bpmnModel;
    }

    /**
     * 验证bpmn的model是否生效
     * @param bpmnModel
     * @throws Exception
     */
    public void validateBpmnModel(BpmnModel bpmnModel) throws Exception {
        ProcessValidatorFactory processValidatorFactory = new ProcessValidatorFactory();
        ProcessValidator defaultProcessValidator = processValidatorFactory.createDefaultProcessValidator();
        List<ValidationError> errors =  defaultProcessValidator.validate(bpmnModel);
        if (CollectionUtils.isNotEmpty(errors)) {
            throw new ClientException("");
        }
    }

}
