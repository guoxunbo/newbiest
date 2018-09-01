package com.newbiest.common.workflow.model;

import com.newbiest.base.model.NBUpdatable;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * 所有需要用到流程相关的实例都必须继承此类
 * Created by guoxunbo on 2018/8/23.
 */
@MappedSuperclass
@Data
public class NBWorkFlow extends NBUpdatable{

    @Column(name="PROCESS_INSTANCE_ID")
    private String processInstanceId;


}
