package com.newbiest.vanchip.dto.mes.issue;

import com.newbiest.mms.model.DocumentLine;
import com.newbiest.vanchip.dto.mes.MesRequestBody;
import lombok.Data;

import java.util.List;

@Data
public class IssueMLotRequestBody extends MesRequestBody {

    private List<String> materialLotIds;

    private DocumentLine documentLine;
}
