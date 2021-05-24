package com.newbiest.mms.model;

import com.newbiest.base.model.NBBase;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author guoxunbo
 * @date 4/6/21 2:18 PM
 */
@Entity
@Table(name="MMS_WORK_STATION")
@Data
public class WorkStation extends NBBase {

    @Column(name="IP_ADDRESS")
    private String ipAddress;

    @Column(name = "PRINT_MACHINE_IP_ADDRESS")
    private String printMachineIpAddress;

}
