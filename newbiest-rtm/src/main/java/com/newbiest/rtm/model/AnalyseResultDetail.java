package com.newbiest.rtm.model;

import com.newbiest.base.model.NBBase;
import com.newbiest.base.model.NBUpdatable;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * Created by guoxunbo on 2019/5/27.
 */
@MappedSuperclass
@Accessors(chain = true)
@Data
public class AnalyseResultDetail extends NBBase {

    @Column(name="RESULT_RRN")
    protected Long resultRrn;

    @Column(name="RESERVED1")
    private String reserved1;

    @Column(name="RESERVED2")
    private String reserved2;

    @Column(name="RESERVED3")
    private String reserved3;

    @Column(name="RESERVED4")
    private String reserved4;

    @Column(name="RESERVED5")
    private String reserved5;

    @Column(name="RESERVED6")
    private String reserved6;

    @Column(name="RESERVED7")
    private String reserved7;

    @Column(name="RESERVED8")
    private String reserved8;

    @Column(name="RESERVED9")
    private String reserved9;

    @Column(name="RESERVED10")
    private String reserved10;
}
