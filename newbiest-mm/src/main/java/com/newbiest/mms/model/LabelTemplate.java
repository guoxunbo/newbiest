package com.newbiest.mms.model;

import com.newbiest.base.model.NBBase;
import com.newbiest.base.threadlocal.ThreadLocalContext;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

/**
 * @author guoxunbo
 * @date 4/6/21 2:22 PM
 */
@Entity
@Table(name="MMS_LBL_TEMPLATE")
@Data
public class LabelTemplate extends NBBase {

    public static final String BARTENDER_REPLACE_URL_PARAMETER = "${remote_address}";

    public static final String TYPE_BARTENDER = "Bartender";
    public static final String TYPE_EXCEL = "Excel";

    @Column(name="NAME")
    private String name;

    @Column(name="DESCRIPTION")
    private String description;

    @Column(name="TYPE")
    private String type;

    /**
     * 当type是Bartender的时候
     *  需要指定其IB的地址。如http://${remote_address}:10099/******
     * 当type是excel的时候，则为excel的Template的模板名称
     */
    @Column(name="DESTINATION")
    private String destination;

    @Column(name="PRINT_COUNT")
    private Integer printCount = 1;

    @Transient
    private List<LabelTemplateParameter> labelTemplateParameterList;

    public String getBartenderDestination(WorkStation workStation) {
        return destination.replace(BARTENDER_REPLACE_URL_PARAMETER, workStation.getPrintMachineIpAddress());
    }

}
