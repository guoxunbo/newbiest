package com.newbiest.base.ui.model;

import com.google.common.collect.Lists;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * 用户栏位参考名称 by 区域的
 * Created by guoxunbo on 2018/2/7.
 */
@Entity
@DiscriminatorValue(NBReferenceName.CATEGORY_OWNER)
@Data
public class NBOwnerReferenceName extends NBReferenceName {

    private static final long serialVersionUID = 3116220955270849126L;

}
