package com.newbiest.vanchip.dto.vlm;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@Data
@XmlRootElement(name = "Envelope")
@XmlAccessorType(XmlAccessType.FIELD)
public class VLMRequest implements Serializable {

    @XmlAttribute
    private String xmlns = "http://schemas.xmlsoap.org/soap/envelope/";
}
