package com.newbiest.rtm.model;

import com.newbiest.base.model.NBBase;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 分析类型定义
 * Created by guoxunbo on 2019/5/27.
 */
@Table(name="RTM_ANALYSE_TYPE_DEF")
@Entity
public class AnalyseTypeDef extends NBBase {

    private String name;

    private String description;

}
