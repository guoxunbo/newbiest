package com.newbiest.base.ui.model;

import com.google.common.collect.Lists;
import com.newbiest.base.model.NBBase;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * 参考名称定义
 * Created by guoxunbo on 2018/2/7.
 */
@Entity
@Table(name="NB_REFERENCE_NAME")
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="CATEGORY", discriminatorType = DiscriminatorType.STRING, length = 32)
@Data
public class NBReferenceName extends NBBase {

    private static final long serialVersionUID = -7783610191753111906L;

    public static final String REFERENCE_NAME_LANGUAGE = "Language";

    public static final String CATEGORY_OWNER = "Owner";
    public static final String CATEGORY_SYSTEM = "System";

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name="CATEGORY",insertable = false, updatable = false)
    private String category;

    @OneToMany(fetch= FetchType.LAZY, cascade={CascadeType.REMOVE})
    @OrderBy(value = "seqNo ASC")
    @JoinColumns({ @JoinColumn(name = "REFERENCE_NAME", referencedColumnName = "NAME"),
            @JoinColumn(name = "CATEGORY", referencedColumnName = "CATEGORY")})
    private List<NBReferenceList> referenceList = Lists.newArrayList();

}
