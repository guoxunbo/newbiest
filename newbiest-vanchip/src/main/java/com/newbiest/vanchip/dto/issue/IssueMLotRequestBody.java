package com.newbiest.vanchip.dto.issue;

import com.newbiest.mms.model.MaterialLot;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class IssueMLotRequestBody implements Serializable {

    public static final String ACTION_TYPE_ISSUE_MLOT = "IssueMLot" ;
    public static final String ACTION_TYPE_ISSUE_MATERIAL = "IssueMaterial" ;

    private String actionType;

    private List<MaterialLot> wmsMaterialLots;

}
