package com.newbiest.base.model;

import com.newbiest.base.utils.PropertyUtils;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;

/**
 * 对象关系配置类 比如在删除的时候需要删除相应的对应信息，因不同业务场景需要删除不同东西，故产生此类
 * Created by guoxunbo on 2017/10/13.
 */
@Entity
@Table(name="NB_RELATION")
@Data
@NoArgsConstructor
public class NBRelation extends NBBase {

    public static final String RELATION_TYPE_SQL = "Sql";
    public static final String RELATION_TYPE_CLASS = "Class";

    @Column(name="SOURCE")
    private String source;

    @Column(name="TARGET")
    private String target;

    @Column(name="SOURCE_RELATION_FIELD")
    private String sourceRelationField;

    @Column(name="TARGET_RELATION_FIELD")
    private String targetRelationField;

    /**
     * 关联类型1. SQL 2. CLASS
     */
    @Column(name="RELATION_TYPE")
    private String relationType = RELATION_TYPE_CLASS;

    public String getWhereClause(NBBase nbBase) {
        Object value = PropertyUtils.getProperty(nbBase, sourceRelationField);
        return targetRelationField + " = '" + value + "'";
    }

}
