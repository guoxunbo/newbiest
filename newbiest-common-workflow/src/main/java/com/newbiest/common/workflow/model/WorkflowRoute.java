package com.newbiest.common.workflow.model;

import lombok.Data;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.List;

/**
 * Created by guoxunbo on 2018/8/14.
 */
@Entity
@DiscriminatorValue(WorkflowDefinition.CATEGORY_TYPE_ROUTE)
@Data
public class WorkflowRoute extends WorkflowDefinition {

    @Transient
    private List<WorkflowStep> steps;

}
