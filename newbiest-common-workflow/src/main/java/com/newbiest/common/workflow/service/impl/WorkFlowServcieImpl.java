package com.newbiest.common.workflow.service.impl;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.threadlocal.SessionContext;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.common.workflow.model.WorkflowDefinition;
import com.newbiest.common.workflow.model.WorkflowStep;
import com.newbiest.common.workflow.service.WorkFlowServcie;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.task.api.Task;
import org.flowable.validation.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by guoxunbo on 2018/8/21.
 */
@Slf4j
@Transactional
@Service
public class WorkFlowServcieImpl implements WorkFlowServcie {

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    TaskService taskService;

    /**
     * 保存流程 如果是Process或者Route的话创建BPMNModel 并验证bpmnModel是否正确
     * @param definition 流程对象
     * @return
     */
    public WorkflowDefinition saveWorkFlowDefinition(WorkflowDefinition definition, SessionContext sc) throws ClientException{
        try {
            // TODO
//            repositoryService.createDeployment().addBpmnModel().deploy();

            return definition;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 删除流程 如果是Process或者Route的话删除相应的工作流信息
     * @param definition 流程对象
     * @throws ClientException
     */
    public void deleteWorkflowDefinition(WorkflowDefinition definition, SessionContext sc) throws ClientException{
        try {
            deleteWorkflowDefinition(definition, false, sc);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 删除流程 如果是Process或者Route的话删除相应的工作流信息
     * @param definition 流程对象
     * @param cascade 是否强制删除已经启动的流程实例
     * @throws ClientException
     */
    public void deleteWorkflowDefinition(WorkflowDefinition definition, boolean cascade, SessionContext sc) throws ClientException{
        try {
            if (!(definition instanceof WorkflowStep)) {
                ProcessDefinition processDefinition = getProcessDefinitionByWorkflowDefinition(definition);
                repositoryService.deleteDeployment(processDefinition.getDeploymentId(), cascade);
            }
            // TODO 删除defnition信息
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 生成图片
     * @param definition
     * @throws ClientException
     */
    public InputStream getWorkFlowDefinitionImage(WorkflowDefinition definition) throws ClientException{
        InputStream inputStream = null;
        try {
            ProcessDefinition processDefinition = getProcessDefinitionByWorkflowDefinition(definition);
            List<String> deploymentResourceNames = repositoryService.getDeploymentResourceNames(processDefinition.getDeploymentId());
            if (!CollectionUtils.isNotEmpty(deploymentResourceNames)) {
                throw new ClientException("");
            }
            String imageName = "";
            for (String resourceName : deploymentResourceNames) {
                if (resourceName.indexOf(".png") != 0) {
                    imageName = resourceName;
                }
            }
            if (StringUtils.isNullOrEmpty(imageName)) {
                throw new ClientException("");
            }
            inputStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), imageName);
            return inputStream;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {

                }
            }
        }
    }

    /**
     * 启动流程
     * @param businessKey 业务ID，比如批次的主键，流程创建人的主键等等 保证唯一
     * @param definition 流程
     * @param sc
     */
    public void startProcess(String businessKey, WorkflowDefinition definition, SessionContext sc) throws ClientException {
        try {
            ProcessDefinition processDefinition = getProcessDefinitionByWorkflowDefinition(definition);
            runtimeService.startProcessInstanceById(processDefinition.getId(), businessKey);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 完成任务
     * @throws ClientException
     */
    public void completeTask(WorkflowStep step, SessionContext sc) throws ClientException {
        try {
            //TODO
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据businessKey获取该业务的未来流程信息
     * @param businessKey
     */
    public List<Task> getFutureFlow(String businessKey) {
        try {
            List<Task> task = taskService.createTaskQuery().processInstanceBusinessKey(businessKey).list();
            return task;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    private ProcessDefinition getProcessDefinitionByWorkflowDefinition(WorkflowDefinition definition) throws ClientException{
        try {
            List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery().processDefinitionName(definition.getId().toString()).list();
            if (!CollectionUtils.isNotEmpty(processDefinitions)) {
                throw new ClientException("");
            }
            return processDefinitions.get(0);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 验证bpmn的model是否生效
     * @param bpmnModel
     * @throws Exception
     */
    private void validateBpmnModel(BpmnModel bpmnModel) throws Exception {
        List<ValidationError> validationErrors = repositoryService.validateProcess(bpmnModel);
        if (CollectionUtils.isNotEmpty(validationErrors)) {
            throw new ClientException("");
        }
    }

    /**
     * 根据WorkFlowDefinition获取BpmnModel
     * @param definition 流程对象
     * @return
     * @throws ClientException
     */
    public BpmnModel getBpmnModelByWorkflowDefnition(WorkflowDefinition definition) throws ClientException {
        try {
            if (definition instanceof WorkflowStep) {
                throw new ClientException("");
            }
            ProcessDefinition processDefinition = getProcessDefinitionByWorkflowDefinition(definition);
            return repositoryService.getBpmnModel(processDefinition.getId());
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);

        }
    }
}
