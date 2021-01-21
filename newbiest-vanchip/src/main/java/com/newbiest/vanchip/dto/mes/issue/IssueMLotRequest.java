package com.newbiest.vanchip.dto.mes.issue;

import com.newbiest.vanchip.dto.mes.MesRequest;
import lombok.Data;

@Data
public class IssueMLotRequest extends MesRequest {

    public static final String MESSAGE_NAME = "IssueMLot";

    private IssueMLotRequestBody body;

}
