package com.newbiest.vanchip.dto.erp.backhaul.scrap;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.newbiest.vanchip.dto.erp.ErpRequest;
import lombok.Data;

import java.util.List;

@Data
@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY,getterVisibility= JsonAutoDetect.Visibility.NONE)
public class ScrapRequest extends ErpRequest {

    private ScrapRequestHeader Header;

    private List<ScrapRequestItem> Item;

}
