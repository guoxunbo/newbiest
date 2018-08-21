package com.newbiest.common.workflow.model;

import com.newbiest.base.model.NBBase;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by guoxunbo on 2018/8/13.
 */
@Data
@Entity
@Table(name="WF_FILE_DEFINITION")
public class WorkflowFileDefinition extends NBBase {

    @Column(name="NAME")
    private String name;


}
