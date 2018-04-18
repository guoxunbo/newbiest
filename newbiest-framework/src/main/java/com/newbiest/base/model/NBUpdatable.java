package com.newbiest.base.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.newbiest.base.utils.DateUtils;
import lombok.Data;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import java.util.Date;

/**
 * 需要管控操作人 操作时间的POJO基类
 * Created by guoxunbo on 2017/9/7.
 */
@MappedSuperclass
@Data
public class NBUpdatable extends NBBase {

    /**
     * 东八区 GMT Plus Eight
     */
    public static final String GMT_PE = "GMT+8";

    @Column(name="CREATED", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(timezone = GMT_PE,pattern = DateUtils.DEFAULT_DATETIME_PATTERN)
    protected Date created;

    @Column(name="CREATED_BY", updatable = false)
    protected String createdBy;

    @Column(name="UPDATED")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(timezone = GMT_PE,pattern = DateUtils.DEFAULT_DATETIME_PATTERN)
    protected Date updated;

    @Column(name="UPDATED_BY")
    protected String updatedBy;

    @Version
    @Column(name="LOCK_VERSION")
    private Long lockVersion = 1L;

    @PrePersist
    private void prePersist() {
        created = new Date();
        updated = new Date();
    }

    @PreUpdate
    private void preUpdate() {
        updated = new Date();
    }

}
