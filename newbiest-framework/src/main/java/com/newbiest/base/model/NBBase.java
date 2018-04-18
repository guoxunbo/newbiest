package com.newbiest.base.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

/**
 * 所有的POJO基类
 * Created by guoxunbo on 2017/9/7.
 */
@MappedSuperclass
@ToString
@Accessors(chain = true)
public class NBBase implements Serializable, Cloneable {

    private static final long serialVersionUID = -4455314847475119658L;

    public static final String BASE_CONDITION = " activeFlag = 'Y' AND (orgRrn = :orgRrn OR orgRrn = 0) ";
    public static final String SQL_BASE_CONDITION = " ACTIVE_FLAG = 'Y' AND (ORG_RRN = :orgRrn OR ORG_RRN = 0) ";

    /**
     * 懒加载时候使用：注意使用many.size()方法获得many对象
     * 这个表示将one对象上所有属性都获取以及获取多对象
     */
    public static final String LAZY_LOAD_PROP = "javax.persistence.loadgraph";

    /**
     * 懒加载时候使用：注意使用many.size()方法获得many对象
     * 这个表示只one对象上的多对象
     */
    public static final String LAZY_FETCH_PROP = "javax.persistence.fetchgraph";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="OBJECT_RRN")
    @XmlElement(name="ObjectRrn")
    @Getter
    @Setter
    private Long objectRrn;

    @Column(name="ORG_RRN")
    @XmlElement(name="OrgRrn")
    @Getter
    @Setter
    private Long orgRrn = 0L;

    @Column(name="ACTIVE_FLAG")
    @XmlElement(name="ActiveFlag")
    private String activeFlag = "Y";

    public Boolean getActiveFlag() {
        return "Y".equalsIgnoreCase(activeFlag);
    }

    public void setActiveFlag(Boolean activeFlag) {
        this.activeFlag = activeFlag ? "Y" : "N";
    }

}
