package com.newbiest.common.workflow;

import com.google.common.io.Files;
import com.newbiest.common.workflow.dto.ProcessDefinition;
import com.newbiest.common.workflow.model.WorkflowRoute;
import com.newbiest.common.workflow.utils.WorkflowUtils;
import org.assertj.core.util.Lists;
import org.flowable.bpmn.BpmnAutoLayout;
import org.flowable.bpmn.model.*;
import org.flowable.bpmn.model.Process;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.image.ProcessDiagramGenerator;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 具备IF判断走向和判断是否跳步的流程测试
 * Created by guoxunbo on 2018/8/23.
 */
public class FlowableGateawayTest extends FlowableNormalTest {

    private String lotId = "TestLot7";

    @Test
    public void deploymentGeteawayRouteTest() {
        try {
            BpmnModel bpmnModel = new BpmnModel();
            //TODO 根据工艺创建流程
            Process process = new Process();
            process.setId("TestGeteaway");
            process.setName("TestGeteaway");

            process.addFlowElement(WorkflowUtils.generateStartEvent());

            SequenceFlow sequenceFlow = WorkflowUtils.generateSequenceFlow(WorkflowUtils.START_EVENT_ID, "S1");
            process.addFlowElement(sequenceFlow);

            ReceiveTask receiveTask = new ReceiveTask();
            receiveTask.setId("S1");
            receiveTask.setName("S1");
            process.addFlowElement(receiveTask);


            sequenceFlow = WorkflowUtils.generateSequenceFlow("S1", WorkflowUtils.EXCLUSIVE_GATEWAY_ID);
            process.addFlowElement(sequenceFlow);

            ExclusiveGateway exclusiveGateway = WorkflowUtils.generateExclusiveGateway();
            process.addFlowElement(exclusiveGateway);

            sequenceFlow = WorkflowUtils.generateSequenceFlowWithCondition(WorkflowUtils.EXCLUSIVE_GATEWAY_ID, "S2-1", "${count > 100}");
            process.addFlowElement(sequenceFlow);

            receiveTask = new ReceiveTask();
            receiveTask.setId("S2-1");
            receiveTask.setName("S2-1");
            process.addFlowElement(receiveTask);

            sequenceFlow = WorkflowUtils.generateSequenceFlowWithCondition(WorkflowUtils.EXCLUSIVE_GATEWAY_ID, "S2-2", "${count <= 100}");
            process.addFlowElement(sequenceFlow);

            receiveTask = new ReceiveTask();
            receiveTask.setId("S2-2");
            receiveTask.setName("S2-2");
            process.addFlowElement(receiveTask);

            sequenceFlow = WorkflowUtils.generateSequenceFlow("S2-1", "S3");
            process.addFlowElement(sequenceFlow);

            receiveTask = new ReceiveTask();
            receiveTask.setId("S3");
            receiveTask.setName("S3");
            process.addFlowElement(receiveTask);

            sequenceFlow = WorkflowUtils.generateSequenceFlow("S2-2", "S4");
            process.addFlowElement(sequenceFlow);

            receiveTask = new ReceiveTask();
            receiveTask.setId("S4");
            receiveTask.setName("S4");
            process.addFlowElement(receiveTask);

            sequenceFlow = WorkflowUtils.generateSequenceFlow("S3", "S5");
            process.addFlowElement(sequenceFlow);

            sequenceFlow = WorkflowUtils.generateSequenceFlow("S4", "S5");
            process.addFlowElement(sequenceFlow);

            receiveTask = new ReceiveTask();
            receiveTask.setId("S5");
            receiveTask.setName("S5");
            process.addFlowElement(receiveTask);

            sequenceFlow = WorkflowUtils.generateSequenceFlowWithSkipCondition("S5", "S6", "${skip == true}");
            process.addFlowElement(sequenceFlow);

            receiveTask = new ReceiveTask();
            receiveTask.setId("S6");
            receiveTask.setName("S6");
            process.addFlowElement(receiveTask);

            sequenceFlow = WorkflowUtils.generateSequenceFlow("S6", WorkflowUtils.END_EVENT_ID);
            process.addFlowElement(sequenceFlow);
            process.addFlowElement(WorkflowUtils.generateEndEvent());

            bpmnModel.addProcess(process);
            new BpmnAutoLayout(bpmnModel).execute();
            Deployment deployment = repositoryService.createDeployment().addBpmnModel(bpmnModel.getProcesses().get(0).getName()+".bpmn", bpmnModel).deploy();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void startProcessTest() {
        WorkflowRoute route = new WorkflowRoute();
        route.setName("TestGeteaway");
        org.flowable.engine.repository.ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionName(route.getId().toString()).singleResult();

        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId(), lotId);
        assert  processInstance != null;
    }

    @Test
    public void leaveTest() {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(lotId).singleResult();
        Execution execution = runtimeService.createExecutionQuery().processInstanceId(processInstance.getId()).activityId("S1").singleResult();
        Map<String, Object> map = new HashMap<>();
        map.put("count", 100);
        runtimeService.trigger(execution.getId(), map);

        processInstance = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(lotId).singleResult();
        execution = runtimeService.createExecutionQuery().processInstanceId(processInstance.getId()).activityId("S2-2").singleResult();
        runtimeService.trigger(execution.getId(), map);

        processInstance = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(lotId).singleResult();
        execution = runtimeService.createExecutionQuery().processInstanceId(processInstance.getId()).activityId("S4").singleResult();
        runtimeService.trigger(execution.getId(), map);

        processInstance = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(lotId).singleResult();
        execution = runtimeService.createExecutionQuery().processInstanceId(processInstance.getId()).activityId("S5").singleResult();
        map = new HashMap<>();
        map.put("skip", true);
        runtimeService.trigger(execution.getId(), map);

    }

}
