package com.newbiest.common.workflow;

import com.google.common.io.Files;
import com.newbiest.common.workflow.model.WorkflowRoute;
import com.newbiest.common.workflow.model.WorkflowStep;
import com.newbiest.common.workflow.utils.WorkflowUtils;
import org.assertj.core.util.Lists;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.*;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.image.ProcessDiagramGenerator;
import org.flowable.validation.ValidationError;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by guoxunbo on 2018/8/22.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class FlowableTest {

    private String lotId = "TestLot3";

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    TaskService taskService;

    @Autowired
    HistoryService historyService;

    @Autowired
    private ProcessEngine processEngine;

    @Test
    public void deploymentNormalRouteTest() {
        try {
            WorkflowRoute route = new WorkflowRoute();
            route.setObjectRrn(1L);
            route.setName("TestRoute3");

            List<WorkflowStep> steps = Lists.newArrayList();

            WorkflowStep step = new WorkflowStep();
            step.setObjectRrn(2L);
            step.setName("S1");
            step.setSeqNo(1L);
            steps.add(step);

            step = new WorkflowStep();
            step.setObjectRrn(3L);
            step.setName("S2");
            step.setSeqNo(2L);
            steps.add(step);

            step = new WorkflowStep();
            step.setObjectRrn(4L);
            step.setName("S3");
            step.setSeqNo(3L);
            steps.add(step);

            route.setSteps(steps);

            BpmnModel bpmnModel = WorkflowUtils.generateBpmnModelByWorkFlowDefinition(route);
            List<ValidationError> validationErrors = repositoryService.validateProcess(bpmnModel);
            Deployment deployment = repositoryService.createDeployment().addBpmnModel(bpmnModel.getProcesses().get(0).getName()+".bpmn", bpmnModel).deploy();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void startProcessTest() {
        WorkflowRoute route = new WorkflowRoute();
        route.setName("TestRoute3");
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionName(route.getId().toString()).singleResult();

        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId(), lotId);
        assert  processInstance != null;
    }

    @Test
    public void leaveTest() {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(lotId).singleResult();
        Execution execution = runtimeService.createExecutionQuery().processInstanceId(processInstance.getId()).activityId("S1").singleResult();
        runtimeService.trigger(execution.getId());

        execution = runtimeService.createExecutionQuery().processInstanceId(processInstance.getId()).activityId("S2").singleResult();
        runtimeService.trigger(execution.getId());

        execution = runtimeService.createExecutionQuery().processInstanceId(processInstance.getId()).activityId("S3").singleResult();
        runtimeService.trigger(execution.getId());
    }

    @Test
    public void historyTest() {
        // 整个业务的持续时间
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().processInstanceBusinessKey(lotId).list();
        String instanceId = "";
        for (HistoricProcessInstance hpi : list) {
            instanceId = hpi.getId();
            System.out.println("流程定义文件->" + hpi.getProcessDefinitionId());
            System.out.println("ProcessInstance->" + hpi.getId());
            System.out.println("StartActivityId->" + hpi.getStartActivityId());
            System.out.println("EndActivityId->" + hpi.getEndActivityId());
            System.out.println("startTime ->" + hpi.getStartTime() + "EndTime->" + hpi.getEndTime() + "duration" + hpi.getDurationInMillis());
        }


        //业务流转过程中每站的记录
        List<HistoricActivityInstance> list1 = historyService.createHistoricActivityInstanceQuery().processInstanceId(instanceId).list().stream().sorted(Comparator.comparing(HistoricActivityInstance :: getStartTime)).collect(Collectors.toList());
        for (HistoricActivityInstance historicActivityInstance : list1) {
            System.out.println("------");
            System.out.println(historicActivityInstance.getActivityId());
            System.out.println(historicActivityInstance.getActivityName());
            System.out.println(historicActivityInstance.getActivityType());
            System.out.println("startTime ->" + historicActivityInstance.getStartTime() + "EndTime->" + historicActivityInstance.getEndTime() + "duration" + historicActivityInstance.getDurationInMillis());

        }

    }

    /**
     * 通过BPMNModel创建的流程没有坐标信息。故无法生成图片
     */
    @Test
    public void createProcessInstanceImage() {
        try {
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(lotId).singleResult();

            List<Execution> executions = runtimeService.createExecutionQuery().processInstanceId(processInstance.getId()).list();
            System.out.println(executions.size());
            List<String> activityIds = Lists.newArrayList();

            for (Execution exe : executions) {
                List<String> ids = runtimeService.getActiveActivityIds(exe.getId());
                activityIds.addAll(ids);
            }

            BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());

            ProcessEngineConfiguration engconf = processEngine.getProcessEngineConfiguration();
            ProcessDiagramGenerator diagramGenerator = engconf.getProcessDiagramGenerator();
            InputStream in = diagramGenerator.generateDiagram(bpmnModel, "png", activityIds, Lists.newArrayList(), engconf.getActivityFontName(), engconf.getLabelFontName(), engconf.getAnnotationFontName(), engconf.getClassLoader(), 1.0);

            byte[] buffer = new byte[in.available()];
            in.read(buffer);
            File targetFile = new File("/Users/apple/Documents/newbiest/newbiest-common-workflow/src/test/java/com/newbiest/common/workflow/" + lotId + ".png");
            Files.write(buffer, targetFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
