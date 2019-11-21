package com.newbiest.im.model;

import com.newbiest.base.model.NBBase;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * webservice定义类 用于给调用外部的webservice接口进行定义
 * Created by guoxunbo on 2019-11-07 17:14
 */
@Data
@Entity
@Table(name = "COM_IM_WS_DEF")
public class WSDefinition extends NBBase {

    /**
     * 接口主键
     */
    @Column(name="IM_RRN")
    private Long imRrn;

    /**
     * 接口名称
     */
    @Column(name="IM_NAME")
    private String imName;

    /**
     * 环境。TEST/DEV/PROD等
     */
    @Column(name="ENV")
    private String env;

    /**
     * URL地址
     */
    @Column(name="URL")
    private String url;

    /**
     * ws对应的用户名
     */
    @Column(name="USERNAME")
    private String username;

    /**
     * ws对应的密码
     */
    @Column(name="PASSWORD")
    private String password;

}
