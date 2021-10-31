package com.newbiest.vanchip.dto.vlm;

import lombok.Data;

import javax.xml.bind.annotation.*;
import java.io.Serializable;

@Data
@XmlRootElement(name = "Envelope")
@XmlAccessorType(XmlAccessType.FIELD)
public class VLMRequest implements Serializable {

    @XmlAttribute
    private String xmlns = "http://schemas.xmlsoap.org/soap/envelope/";

    @XmlElement(name ="Body")
    private VLMRequestBody body;
}
