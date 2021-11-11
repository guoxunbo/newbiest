package com.newbiest.vanchip.dto.vlm.box;

import com.newbiest.vanchip.dto.vlm.VLMRequest;
import lombok.Data;

import javax.xml.bind.annotation.*;

@Data
@XmlRootElement(name = "Envelope")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetBoxIDRequest extends VLMRequest {

    @XmlElement(name ="Body")
    private GetBoxIDRequestBody body;
}
