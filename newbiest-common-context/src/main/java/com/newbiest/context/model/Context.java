package com.newbiest.context.model;

import com.newbiest.base.model.NBBase;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by guoxunbo on 2018/7/6.
 */
@Entity
@Table(name = "COM_CONTEXT")
@Data
@NoArgsConstructor
public class Context extends NBBase{

    @Column(name="NAME")
    private String name;

    @Column(name="DESCRIPTION")
    private String description;
      
}
