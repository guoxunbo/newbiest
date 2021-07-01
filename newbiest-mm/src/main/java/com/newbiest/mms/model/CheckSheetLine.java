package com.newbiest.mms.model;

import com.newbiest.base.model.NBBase;
import lombok.Data;

import javax.persistence.*;

/**
 * 检查表单明细
 * @author guoxunbo
 * @date 12/23/20 10:41 AM
 */
@Table(name="MMS_CHECK_SHEET_LINE")
@Entity
@Data
public class CheckSheetLine extends NBBase {

    @Column(name="CHECK_SHEET_RRN")
    private String checkSheetRrn;

    @Column(name="NAME")
    private String name;

    @Column(name="DESCRIPTION")
    private String description;

    /**
     * 抽样方案
     */
    @Column(name="SAMPLING_SCHEME")
    private String samplingScheme ;
}
