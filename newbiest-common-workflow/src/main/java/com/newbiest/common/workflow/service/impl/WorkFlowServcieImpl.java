package com.newbiest.common.workflow.service.impl;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.common.workflow.model.WorkflowDefinition;
import com.newbiest.common.workflow.model.WorkflowStep;
import com.newbiest.common.workflow.service.WorkFlowServcie;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.flowable.engine.repository.ProcessDefinition;
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

    /**
     * 保存流程 如果是Process或者Route的话创建BPMNModel
     * @param definition 流程对象
     * @return
     */
    public WorkflowDefinition saveWorkFlowDefinition(WorkflowDefinition definition, SessionContext sc) throws ClientException{
        try {
            // TODO
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
    public void deleteWorkflowDefnition(WorkflowDefinition definition, SessionContext sc) throws ClientException{
        try {
            deleteWorkflowDefnition(definition, false, sc);
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
    public void deleteWorkflowDefnition(WorkflowDefinition definition, boolean cascade, SessionContext sc) throws ClientException{
        try {
            if (!(definition instanceof WorkflowStep)) {
                ProcessDefinition processDefinition = getProcessDefinitionByWorkflowDefinition(definition);
                repositoryService.deleteDeployment(processDefinition.getDeploymentId(), cascade);
            }
            // 删除defnition信息
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


    private ProcessDefinition getProcessDefinitionByWorkflowDefinition(WorkflowDefinition definition) throws ClientException{
        try {
            List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery().processDefinitionId(definition.getId()).list();
            if (!CollectionUtils.isNotEmpty(processDefinitions)) {
                throw new ClientException("");
            }
            return processDefinitions.get(0);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据WorkFlowDefnition获取BpmnModel
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
