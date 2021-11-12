package com.newbiest.gc.rest.scm.engManager;

import com.newbiest.msg.ResponseBody;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class EngManagerResponseBody extends ResponseBody {

    private List<Map<String, String>> materialLotList;
}
