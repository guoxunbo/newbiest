package com.newbiest.common.idgenerator.model;

import com.newbiest.base.model.NBUpdatable;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 序列号类
 * Created by guoxunbo on 2018/8/6.
 */
@Entity
@Table(name="COM_GENERATOR_SEQUENCE")
@Data
public class Sequence extends NBUpdatable {

    @Column(name = "NAME")
    private String name;

    @Column(name = "NEXT_SEQ")
    private Long nextSeq;

    @Column(name = "GENERATOR_LINE_RRN")
    private Long generatorLineRrn;

}
