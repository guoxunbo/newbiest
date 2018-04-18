package com.newbiest.security.model;

import com.newbiest.base.model.NBBase;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 区域
 * Created by guoxunbo on 2017/9/11.
 */
@Entity
@Table(name="NB_ORG")
@NoArgsConstructor
@Data
public class NBOrg extends NBBase {

    public static final Long GLOBAL_ORG_RRN = 0L;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

}
