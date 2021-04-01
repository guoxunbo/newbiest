package com.newbiest.vanchip.rest.storage;

import com.newbiest.base.msg.ResponseBody;
import com.newbiest.mms.model.Storage;
import lombok.Data;

@Data
public class StorageResponseBody extends ResponseBody {

    private static final long serialVersionUID = 1L;

    private Storage storage;
}
