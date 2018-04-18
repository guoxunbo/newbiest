package com.newbiest.base.ui.model;

import com.newbiest.base.model.NBBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * 系统栏位参考值
 * Created by guoxunbo on 2018/2/7.
 */
@Entity
@DiscriminatorValue("SYSTEM")
public class NBSystemReferenceList extends NBReferenceList {

    private static final long serialVersionUID = 2282157532805072358L;
}
