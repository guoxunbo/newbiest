package com.newbiest.base.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.newbiest.base.utils.DateUtils;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * Created by guoxunbo on 2019/2/26.
 */
@MappedSuperclass
@Data
@NoArgsConstructor
public class NBVersionControlHis extends NBHis {

    private static final long serialVersionUID = 2047195882683047927L;

    public static final String TRANS_TYPE_CREATE_AND_ACTIVE = "CreateAndActive";
    public static final String TRANS_TYPE_ACTIVE = "Active";
    public static final String TRANS_TYPE_UNFROZEN = "Unfrozen";
    public static final String TRANS_TYPE_FROZEN = "Frozen";
    public static final String TRANS_TYPE_INACTIVE = "Inactive";

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name="VERSION")
    private Long version;

    @Column(name="STATUS")
    private String status;

    @Column(name="ACTIVE_TIME")
    @JsonFormat(timezone = GMT_PE,pattern = DateUtils.DEFAULT_DATETIME_PATTERN)
    @Temporal(TemporalType.TIMESTAMP)
    private Date activeTime;

    @Column(name="ACTIVE_USER")
    private String activeUser;

}
