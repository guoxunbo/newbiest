package com.newbiest.rtm.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * Created by guoxunbo on 2019/5/27.
 */
@Data
@Entity
@Table(name="DYNAX_ANALYSE_RESULT")
public class DynaxAnalyseResult extends AnalyseResult {

    @Column(name="FILE_NAME")
    private String fileName;

    @Column(name="PART_NAME")
    private String partName;

    @Column(name="LOT_ID")
    private String lotId;

    @Column(name="SITE_NUMBER")
    private String siteNumber;

    @Column(name="BATCH_NUMBER")
    private String batchNumber;

    @Column(name="PART_NUMBER")
    private String partNumber;

    @OneToMany(fetch= FetchType.LAZY, cascade={CascadeType.ALL})
    @JoinColumn(name = "RESULT_RRN", referencedColumnName = "OBJECT_RRN")
    private List<DynaxAnalyseResultDetail> details;

}
