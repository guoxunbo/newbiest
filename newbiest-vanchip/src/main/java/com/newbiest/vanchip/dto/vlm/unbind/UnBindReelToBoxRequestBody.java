package com.newbiest.vanchip.dto.vlm.unbind;

import com.newbiest.vanchip.dto.vlm.VLMRequestBody;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement(name = "Body")
@XmlAccessorType(XmlAccessType.FIELD)
public class UnBindReelToBoxRequestBody extends VLMRequestBody {

    @XmlElement(name ="UnbindBox")
    private UnBindReelToBox unBindReelToBox;
}
