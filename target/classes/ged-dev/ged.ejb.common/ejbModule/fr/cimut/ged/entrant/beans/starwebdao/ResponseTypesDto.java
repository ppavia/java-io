package fr.cimut.ged.entrant.beans.starwebdao;

import java.util.ArrayList;
import java.util.List;

/**
 * StarwebDAO /types Response
 */
public class ResponseTypesDto {

	private List<ResponseTypeDto> types = new ArrayList<ResponseTypeDto>();

	public ResponseTypesDto() {
	}

	public List<ResponseTypeDto> getTypes() {
		return types;
	}

	public void setTypes(List<ResponseTypeDto> types) {
		this.types = types;
	}
}
