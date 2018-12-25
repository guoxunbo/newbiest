package com.newbiest.base.ui.model;

import com.newbiest.base.model.NBBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * 栏位参考值
 * Created by guoxunbo on 2018/2/7.
 */
@Entity
@Table(name="NB_REFERENCE_LIST")
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="CATEGORY", discriminatorType = DiscriminatorType.STRING, length = 32)
@Data
@EqualsAndHashCode(callSuper = true)
public class NBReferenceList extends NBBase{

    private static final long serialVersionUID = -5309380552527153511L;

    public static final String CATEGORY_OWNER = "Owner";
    public static final String CATEGORY_SYSTEM = "System";


    @Column(name = "REFERENCE_NAME")
    private String referenceName;

    @Column(name = "\"KEY\"")
    private String key;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "VALUE")
    private String value;

    @Column(name = "SEQ_NO")
    private Integer seqNo = 0;

    @Column(name="CATEGORY", updatable = false, insertable = false)
    private String category;

}
