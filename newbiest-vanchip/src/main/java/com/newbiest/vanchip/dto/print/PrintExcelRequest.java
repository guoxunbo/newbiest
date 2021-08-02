package com.newbiest.vanchip.dto.print;

import com.newbiest.vanchip.dto.mes.MesRequest;
import lombok.Data;

@Data
public class PrintExcelRequest extends MesRequest {

    public static final String MESSAGE_NAME = "IssueMLot";

    private PrintExcelRequestBody body;

}
