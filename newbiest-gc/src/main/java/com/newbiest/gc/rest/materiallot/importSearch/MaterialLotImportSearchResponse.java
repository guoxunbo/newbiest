package com.newbiest.gc.rest.materiallot.importSearch;

import com.newbiest.msg.Response;
import lombok.Data;

@Data
public class MaterialLotImportSearchResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private MaterialLotImportSearchResponseBody body;
	
}
