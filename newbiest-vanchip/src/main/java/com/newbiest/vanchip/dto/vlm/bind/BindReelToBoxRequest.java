package com.newbiest.vanchip.dto.vlm.bind;

import com.newbiest.vanchip.dto.vlm.VLMRequest;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement(name = "Envelope")
@XmlAccessorType(XmlAccessType.FIELD)
public class BindReelToBoxRequest extends VLMRequest {

    @XmlElement(name ="Body")
    private BindReelToBoxRequestBody body;
}
