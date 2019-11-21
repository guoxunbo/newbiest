package com.newbiest.rms.model;

import com.newbiest.base.model.NBUpdatable;
import com.newbiest.base.utils.StringUtils;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="RMS_EQUIPMENT")
@Data
public class Equipment extends NBUpdatable {

    @Column(name="EQUIPMENT_ID")
    private String equipmentId;

    @Column(name="DESCRIPTION")
    private String description;

    @Column(name="EQUIPMENT_TYPE")
    private String equipmentType;

    @Column(name="HOLD_FLAG")
    private String holdFlag;

    @Column(name="COMMUNICATION_FLAG")
    private String communicationFlag;


    public Boolean getCommunicationFlag() {
        return StringUtils.YES.equalsIgnoreCase(communicationFlag);
    }

    public void setCommunicationFlag(Boolean communicationFlag) {
        this.communicationFlag = communicationFlag ? StringUtils.YES : StringUtils.NO;
    }

    public Boolean getHoldFlag() {
        return StringUtils.YES.equalsIgnoreCase(holdFlag);
    }

    public void setHoldFlag(Boolean holdFlag) {
        this.holdFlag = holdFlag ? StringUtils.YES : StringUtils.NO;
    }

}
