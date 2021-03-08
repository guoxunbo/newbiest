package com.newbiest.mms.rest.materiallot.release;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class ReleaseMLotResponse extends Response {

    private static final long serialVersionUID = 1L;

    private ReleaseMLotResponseBody body ;
}
