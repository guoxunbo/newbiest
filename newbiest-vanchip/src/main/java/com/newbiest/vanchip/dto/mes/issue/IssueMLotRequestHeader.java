package com.newbiest.vanchip.dto.mes.issue;

import com.newbiest.vanchip.dto.mes.MesRequestHeader;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IssueMLotRequestHeader extends MesRequestHeader {

    public IssueMLotRequestHeader() {
        super(IssueMLotRequest.MESSAGE_NAME);
    }

}
