package com.newbiest.vanchip.dto.issue;

import lombok.Data;

import java.io.Serializable;

@Data
public class IssueMLotResponse implements Serializable {

        private IssueMLotResponseBody body;

        private IssueMLotResponseHeader header;

}
