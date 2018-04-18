package com.newbiest.base.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;

/**
 * 自定义SQL类
 * Created by guoxunbo on 2017/11/5.
 */
@Entity
@Table(name="NB_QUERY")
@Data
@NoArgsConstructor
public class NBQuery extends NBBase {

    @Column(name="NAME")
    @XmlElement(name="name")
    private String name;

    @Column(name="DESCRIPTION")
    @XmlElement(name="Description")
    private String description;

    @Column(name="QUERY_TEXT")
    @XmlElement(name="QueryText")
    private String queryText;

}
