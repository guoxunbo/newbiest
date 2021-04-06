package com.newbiest.mms.model;

import com.newbiest.base.model.NBBase;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author guoxunbo
 * @date 4/6/21 2:22 PM
 */
@Entity
@Table(name="MMS_LBL_TEMPLATE_PARA")
@Data
public class LabelTemplateParameter extends NBBase {

    @Column(name="LBL_TEMPLATE_RRN")
    private String lblTemplateRrn;

    @Column(name="NAME")
    private String name;

    @Column(name="DEFAULT_VALUE")
    private String defaultValue;

}
