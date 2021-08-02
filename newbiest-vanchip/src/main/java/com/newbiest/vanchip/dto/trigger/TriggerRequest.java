package com.newbiest.vanchip.dto.trigger;

import lombok.Data;

import java.io.Serializable;

@Data
public class TriggerRequest implements Serializable {

    private TriggerRequestHeader header;
    private TriggerRequestBody body;

}
