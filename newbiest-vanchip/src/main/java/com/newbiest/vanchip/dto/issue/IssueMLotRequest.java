package com.newbiest.vanchip.dto.issue;

import lombok.Data;

import java.io.Serializable;

@Data
public class IssueMLotRequest implements Serializable {

        private IssueMLotRequestBody body;

        private IssueMLotRequestHeader header;

}
