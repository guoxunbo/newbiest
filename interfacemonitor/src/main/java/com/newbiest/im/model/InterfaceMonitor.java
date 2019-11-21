package com.newbiest.im.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.newbiest.base.model.NBBase;
import com.newbiest.base.model.NBUpdatable;
import com.newbiest.base.utils.DateUtils;
import lombok.Data;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 所有跟外部系统的接口定义处。不管是请求别人还是别人请求
 * Created by guoxunbo on 2019-11-07 15:48
 */
@Data
@Entity
@Table(name = "COM_IM")
public class InterfaceMonitor extends NBBase {

    public static final String TYPE_WS = "WebService";

    @Column(name="NAME")
    private String name;

    @Column(name="DESCRIPTION")
    private String description;

    @Column(name="BEAN_NAME")
    private String beanName;

    @Column(name="TYPE")
    private String type;

    @Column(name="LAST_EXEC_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(timezone = NBUpdatable.GMT_PE,pattern = DateUtils.DEFAULT_DATETIME_PATTERN)
    private Date lastExecTime;

    public void start() {
        this.lastExecTime = DateUtils.now();
    }


}
