package com.newbiest.base.model;

import com.newbiest.base.utils.StringUtils;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 所有的POJO基类
 * Created by guoxunbo on 2017/9/7.
 */
@MappedSuperclass
@Accessors(chain = true)
@Data
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

    public static final String LANGUAGE_CHINESE = "Chinese";
    public static final String LANGUAGE_ENGLISH = "English";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="OBJECT_RRN")
    protected Long objectRrn;

    @Column(name="ORG_RRN")
    protected Long orgRrn = 0L;

    @Column(name="ACTIVE_FLAG")
    protected String activeFlag = StringUtils.YES;

    public Boolean getActiveFlag() {
        return StringUtils.YES.equalsIgnoreCase(activeFlag);
    }

    public void setActiveFlag(Boolean activeFlag) {
        this.activeFlag = activeFlag ? StringUtils.YES : StringUtils.NO;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        NBBase nbBase = (NBBase) super.clone();
        nbBase.setObjectRrn(null);
        return nbBase;
    }
}
