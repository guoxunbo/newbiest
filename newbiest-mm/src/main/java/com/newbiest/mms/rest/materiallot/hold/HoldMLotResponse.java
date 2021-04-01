package com.newbiest.mms.rest.materiallot.hold;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class HoldMLotResponse extends Response {

    private static final long serialVersionUID = 1L;

    private HoldMLotResponseBody body ;
}
